
package com.example.outdoorsy.model

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val cloudinaryModel: CloudinaryModel


) {


    private val database =firestore // Initializing a firebase instance
    val usersCollection = firestore.collection("users")
    private val postsCollection = firestore.collection("posts")

    companion object {
        // Tag for logging
        private const val TAG = "FirebaseModel"

        // Factory method to create a FirebaseModel instance
        fun create(
            firestore: FirebaseFirestore,
            firebaseAuth: FirebaseAuth,
            cloudinaryModel: CloudinaryModel
        ): FirebaseModel {
            Log.d(TAG, "Creating a new FirebaseModel instance")
            return FirebaseModel(firestore, firebaseAuth, cloudinaryModel)
        }
    }

    fun updateUserProfile(
        fullName: String,
        imageUrl: String?,
        currentImageUrl: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val updates = hashMapOf<String, Any>()
        updates["fullname"] = fullName
        imageUrl?.let { updates["profileImg"] = it }

        currentUser?.let { user ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .setPhotoUri(imageUrl?.let { Uri.parse(it) }) // Only set if imageUrl is not null
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // ✅ Firestore update
                        usersCollection.document(user.uid)
                            .update(updates)
                            .addOnSuccessListener {
                                // ✅ Refresh FirebaseAuth user
                                user.reload().addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        onSuccess()
                                    } else {
                                        onError(it.exception?.localizedMessage ?: "Failed to reload user")
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                onError(e.localizedMessage ?: "An error occurred while updating Firestore")
                            }
                    } else {
                        onError(task.exception?.localizedMessage ?: "Profile update failed")
                    }
                }
        } ?: onError("User not logged in")
    }



    suspend fun getUserPosts(userId: String): List<PostModel> {
        return try {
            val querySnapshot = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(PostModel::class.java)?.copy(postId = document.id)
            }
        } catch (e: Exception) {
            emptyList() // Return an empty list if an error occurs
        }
    }

suspend fun getUsersByName(query: String): List<UserModel> {
    val database = FirebaseFirestore.getInstance()
    return try {
        database.collection("users")
            .orderBy("fullname")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .await()
            .toObjects(UserModel::class.java).also {
                Log.d("Search", "Fetched ${it.size} users for query: $query")
            }
    } catch (e: Exception) {
        Log.e("Search", "Error fetching users: ", e)
        emptyList()
    }
}

    fun logoutUser() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): String? {
        return firebaseAuth.currentUser?.uid
    }
    // Save user to Firestore


    // Update user in Firestore
    suspend fun updateUser(userId: String, updatedData: Map<String, Any>) {
        usersCollection.document(userId).update(updatedData).await()
    }

    // Delete user from Firestore
    suspend fun deleteUser(userId: String) {
        usersCollection.document(userId).delete().await()
    }


    suspend fun getUser(userId: String): UserModel? {
        return try {
            val documentSnapshot = usersCollection.document(userId).get().await()
            documentSnapshot.toObject(UserModel::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseModel", "Error fetching user", e)
            null
        }
    }
    suspend fun loginUser(email: String, password: String): UserModel? {
        try {
            val authResult = FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .await()

            val userId = authResult.user?.uid
            return if (userId != null) {
                val user = getUser(userId) // Fetch user details from Firebase
                user
            } else {
                null // Handle authentication failure
            }
        } catch (e: Exception) {
            return null // Handle authentication error
        }
    }

    suspend fun getPostById(postId: String): PostModel {
        return try {
            val documentSnapshot = firestore.collection("posts")
                .document(postId)
                .get()
                .await()

            documentSnapshot.toObject(PostModel::class.java)?.copy(postId = documentSnapshot.id) ?: PostModel()
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error in getPostById: ${e.message}")
            PostModel() // Return a default empty post if an error occurs
        }
    }


suspend fun addCommentToPost(postId: String, comment: CommentModel): Int {
    return try {
        val postRef = firestore.collection("posts").document(postId)
        val commentRef = postRef.collection("comments").document()

        commentRef.set(comment).await()

        // Atomically increment the comments count and return the updated value
        firestore.runTransaction { transaction ->
            val postSnapshot = transaction.get(postRef)
            val currentCount = postSnapshot.getLong("commentsCount") ?: 0
            val updatedCount = currentCount + 1
            transaction.update(postRef, "commentsCount", updatedCount)
            updatedCount.toInt() // ✅ Ensure this function returns an Int
        }.await()
    } catch (e: Exception) {
        Log.e("FirestoreError", "Error in addCommentToPost: ${e.message}")
        -1 // Return -1 in case of an error
    }
}



    // Helper method to atomically increment the comment count for a post
    private suspend fun incrementCommentsCount(postId: String) {
        try {
            val postRef = firestore.collection("posts").document(postId)
            // Use the FieldValue.increment() for atomic increment
            firestore.runTransaction { transaction ->
                transaction.update(postRef, "commentsCount", FieldValue.increment(1))
            }.await()

        } catch (e: Exception) {
            Log.e("FirestoreError", "Error incrementing comment count: ${e.message}")
        }
    }



    // Toggle like on a post (increment or decrement like count)
    suspend fun toggleLike(postId: String): PostModel {
        return try {
            val postRef = firestore.collection("posts").document(postId)

            firestore.runTransaction { transaction ->
                val postSnapshot = transaction.get(postRef)
                val currentLikes = postSnapshot.getLong("likesCount") ?: 0

                val newLikes = if (currentLikes.toInt() == 0) currentLikes + 1 else currentLikes - 1
                transaction.update(postRef, "likesCount", newLikes)

                // Update and return the post object with the new like count
                postSnapshot.toObject(PostModel::class.java)?.copy(postId = postSnapshot.id, likesCount = newLikes)
            }.await() ?: PostModel()
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error toggeling like: ${e.message}")
            PostModel() // Return an empty post if an error occurs
        }
    }


suspend fun registerUser(email: String, password: String, fullname: String): Result<Unit> {
    return try {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val userId = authResult.user?.uid ?: throw IllegalStateException("User ID not found")
        val user = UserModel(
            id = userId,
            email = email,
            fullname = fullname
        )
        saveUser(user)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

private suspend fun saveUser(user: UserModel): Unit {
    val userData = mapOf(
        "email" to user.email,
        "fullname" to user.fullname,
        "id" to user.id
    )
   usersCollection.document(user.id).set(userData).await()
}

    suspend fun getFollowersCount(userId: String): Int {
        return try {
            val userDocument = database.collection("users").document(userId).get().await()
            userDocument.getLong("followersCount")?.toInt() ?: 0
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching followers count: ${e.message}")
            0
        }
    }


    // Fetch the following count from the user's document
    suspend fun getFollowingCount(userId: String): Int {
        return try {
            val userDocument = database.collection("users").document(userId).get().await()
            userDocument.getLong("followingCount")?.toInt() ?: 0
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching following count: ${e.message}")
            0
        }
    }

    suspend fun getUserById(userId: String): UserModel? {
        return try {
            val documentSnapshot = database.collection("users")
                .document(userId)
                .get()
                .await()

            documentSnapshot.toObject(UserModel::class.java)?.also {
                it.id = documentSnapshot.id // Set the user ID explicitly
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user by ID: ${e.message}")
            null
        }
    }

    suspend fun isFollowing(loggedInUserId: String, profileUserId: String): Boolean {
        return try {
            val loggedInUserFollowingRef = database.collection("users").document(loggedInUserId).collection("following").document(profileUserId)
            val snapshot = loggedInUserFollowingRef.get().await()
            snapshot.exists() // Returns true if the document exists, false otherwise
        } catch (e: Exception) {
            Log.e("UserRepository", "Error checking if following: ${e.message}")
            false // Assume not following if there's an error
        }
    }

    suspend fun toggleFollowUser(profileUserId: String): Boolean {
        val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        return try {
            var didFollow = false
            database.runTransaction { transaction ->
                val loggedInUserRef = database.collection("users").document(loggedInUserId)
                val profileUserRef = database.collection("users").document(profileUserId)
                val loggedInUserFollowingRef = loggedInUserRef.collection("following").document(profileUserId)
                val profileUserFollowersRef = profileUserRef.collection("followers").document(loggedInUserId)

                // Check if already following
                val snapshot = transaction.get(loggedInUserFollowingRef)
                if (snapshot.exists()) {
                    // Unfollow
                    transaction.delete(loggedInUserFollowingRef)
                    transaction.delete(profileUserFollowersRef)
                    transaction.update(loggedInUserRef, "followingCount", FieldValue.increment(-1))
                    transaction.update(profileUserRef, "followersCount", FieldValue.increment(-1))
                } else {
                    // Follow
                    transaction.set(loggedInUserFollowingRef, hashMapOf("timestamp" to FieldValue.serverTimestamp()))
                    transaction.set(profileUserFollowersRef, hashMapOf("timestamp" to FieldValue.serverTimestamp()))
                    transaction.update(loggedInUserRef, "followingCount", FieldValue.increment(1))
                    transaction.update(profileUserRef, "followersCount", FieldValue.increment(1))
                    didFollow = true
                }
            }.await()
            didFollow
        } catch (e: Exception) {
            Log.e("UserRepository", "Error toggling follow: ${e.localizedMessage}")
            false
        }
    }

    suspend fun followUser(loggedInUserId: String, profileUserId: String) {
        updateCounts(loggedInUserId, profileUserId, 1)
    }

    suspend fun unfollowUser(loggedInUserId: String, profileUserId: String) {
        updateCounts(loggedInUserId, profileUserId, -1)
    }

    private fun updateCounts(loggedInUserId: String, profileUserId: String, increment: Int) {
        database.runTransaction { transaction ->
            val profileUserRef = database.collection("users").document(profileUserId)
            val loggedInUserRef = database.collection("users").document(loggedInUserId)

            val profileUser = transaction.get(profileUserRef)
            val loggedInUser = transaction.get(loggedInUserRef)

            val profileFollowersCount = (profileUser.getLong("followersCount") ?: 0) + increment
            val loggedInFollowingCount = (loggedInUser.getLong("followingCount") ?: 0) + increment

            transaction.update(profileUserRef, "followersCount", profileFollowersCount)
            transaction.update(loggedInUserRef, "followingCount", loggedInFollowingCount)
        }
    }







    fun getPostLikesCount(postId: String, callback: (Long) -> Unit) {
        postsCollection.document(postId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val likesCount = snapshot.getLong("likesCount") ?: 0
                    callback(likesCount)
                } else {
                    callback(0)
                }
            }
    }


suspend fun getFeedPosts(userId: String): List<PostModel> {
    return try {
        // Fetch the list of followed users
        val followingIds = firestore.collection("users")
            .document(userId)
            .collection("following")
            .get()
            .await()
            .documents.mapNotNull { it.id }
            .toMutableList() // Convert to mutable list

        // 🔥 Add the logged-in user to the list
        followingIds.add(userId)

        Log.d("FirebaseModel", "Fetching posts from users: $followingIds")

        // 🚀 Fetch posts **without ordering** first (Firestore limitation)
        val query = firestore.collection("posts")
            .whereIn("userId", followingIds)
            .get()
            .await()

        val posts = query.documents.mapNotNull { document ->
            val post = document.toObject(PostModel::class.java)?.copy(postId = document.id)
            Log.d("FirebaseModel", "Fetched Post - ID: ${post?.postId}, UserID: ${post?.userId}, Timestamp: ${post?.timestamp}")
            post
        }


        // ✅ Now manually sort by timestamp in descending order
        val sortedPosts = posts.sortedByDescending { it.timestamp }

        Log.d("FirebaseModel", "Total Feed posts: ${sortedPosts.size}")
        return sortedPosts

    } catch (e: FirebaseFirestoreException) {
        Log.e("FirestoreError", "Firestore error: ${e.message}", e)
        emptyList()
    } catch (e: Exception) {
        Log.e("FirestoreError", "Unexpected error: ${e.message}", e)
        emptyList()
    }
}



    fun fetchPostLikesCount(postId: String, callback: (Long) -> Unit) {
        val postRef = firestore.collection("posts").document(postId)
        postRef.get().addOnSuccessListener { documentSnapshot ->
            val likesCount = documentSnapshot.getLong("likesCount") ?: 0
            callback(likesCount)
        }.addOnFailureListener {
            callback(0)
        }
    }

    // Observe likes count changes in real-time
    fun observePostLikesCount(postId: String, callback: (Long) -> Unit) {
        val postRef = firestore.collection("posts").document(postId)
        postRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("FirebaseModel", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val likesCount = snapshot.getLong("likesCount") ?: 0
                callback(likesCount)
            } else {
                Log.e("FirebaseModel", "Current data: null")
                callback(0)
            }
        }
    }

    suspend fun addLike(postId: String, userId: String) {
        val postRef = firestore.collection("posts").document(postId)
        val userLikeRef = postRef.collection("likes").document(userId)

        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userLikeRef)
                if (!snapshot.exists()) {
                    transaction.set(userLikeRef, mapOf("timestamp" to FieldValue.serverTimestamp()))
                    transaction.update(postRef, "likesCount", FieldValue.increment(1))
                }
            }.await()
        } catch (e: Exception) {
            Log.e("FirebaseModel", "Error adding like: ${e.message}")
        }
    }
    suspend fun removeLike(postId: String, userId: String) {
        val postRef = firestore.collection("posts").document(postId)
        val userLikeRef = postRef.collection("likes").document(userId)

        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userLikeRef)
                if (snapshot.exists()) {
                    transaction.delete(userLikeRef)
                    transaction.update(postRef, "likesCount", FieldValue.increment(-1))
                }
            }.await()
        } catch (e: Exception) {
            Log.e("FirebaseModel", "Error removing like: ${e.message}")
        }
    }

suspend fun toggleLike(postId: String, userId: String): Boolean {
    val postRef = firestore.collection("posts").document(postId)
    val likeRef = postRef.collection("likes").document(userId)

    return firestore.runTransaction { transaction ->
        val likeSnapshot = transaction.get(likeRef)
        val postSnapshot = transaction.get(postRef)
        val currentLikes = postSnapshot.getLong("likesCount") ?: 0

        if (likeSnapshot.exists()) {
            // User is unliking the post
            transaction.delete(likeRef)
            transaction.update(postRef, "likesCount", FieldValue.increment(-1))
            false // Return false indicating the post was unliked
        } else {
            // User is liking the post
            transaction.set(likeRef, hashMapOf("timestamp" to FieldValue.serverTimestamp()))
            transaction.update(postRef, "likesCount", FieldValue.increment(1))
            true // Return true indicating the post was liked
        }
    }.await()
}


    suspend fun checkIfLiked(postId: String, userId: String): Boolean {
        if (postId.isEmpty() || userId.isEmpty()) {
            Log.e("FirebaseModel", "Invalid postId or userId")
            return false
        }
        val likeRef = postsCollection.document(postId).collection("likes").document(userId)
        return try {
            val snapshot = likeRef.get().await()
            snapshot.exists()
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error checking like status: ${e.message}")
            false
        }
    }

fun updatePost(postId: String, textContent: String, newImageUrl: String, oldImageUrl: String?) {




        cloudinaryModel.deleteImageFromCloudinary(oldImageUrl!!,
            onSuccess = {
                Log.d("EditPostViewModel", "Old image deleted successfully")
                updateFirestorePost(postId, textContent, newImageUrl)
                        },
            onError = { error ->
                Log.e("EditPostViewModel", "Error deleting old image: $error")
//                updateFirestorePost(postId, textContent, newImageUrl)
            }
        )


}

    // ✅ Helper function to update Firestore after deleting the old image
    private fun updateFirestorePost(postId: String, textContent: String, newImageUrl: String) {// update func should upload the img to cloudinary
        val updates = mapOf(
            "textContent" to textContent,
            "imageUrl" to newImageUrl
        )

        firestore.collection("posts").document(postId)
            .set(updates, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("EditPostViewModel", "Post updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("EditPostViewModel", "Error updating post", e)
            }
    }


    // Delete a post
    suspend fun deletePost(postId: String) {
        try {
            postsCollection.document(postId).delete().await()
            Log.d("FirebaseModel", "Post deleted successfully")
        } catch (e: Exception) {
            Log.e("FirebaseModel", "Error deleting post", e)
        }
    }


fun uploadImageToCloudinary(bitmap: Bitmap, imageName: String, onSuccess: (String?) -> Unit, onError: (String?) -> Unit){
    cloudinaryModel.uploadImage(bitmap, imageName, onSuccess = { url ->
        if (url != null) {
            Log.d("CameraRepository", "Image uploaded, URL received: $url")
            onSuccess(url)
//            createPost(bitmap, url, onSuccess, onError)  // Create post after getting the URL
        } else {
            onError("Failed to upload image, no URL returned")
        }
    }, onError = onError)


}

    fun createPost(bitmap: Bitmap, imageUrl: String, textContent: String, onSuccess: (String?) -> Unit, onError: (String?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onError("User is not logged in.")
        val postMap = hashMapOf(
            "userId" to userId,
            "imageUrl" to imageUrl,  // Ensure this is the URL from Cloudinary
            "textContent" to textContent,
            "timestamp" to FieldValue.serverTimestamp()
        )

        postsCollection
            .add(postMap)
            .addOnSuccessListener {
                Log.d("FirebaseModel", "Post successfully created with ID: ${it.id}")
                onSuccess(it.id)
            }
            .addOnFailureListener {
                Log.e("FirebaseModel", "Error creating post: ", it)
                onError(it.message)
            }
    }


    fun getPosts(onSuccess: (List<Map<String, Any>>) -> Unit, onFailure: (String) -> Unit){
    postsCollection
    .get()
    .addOnSuccessListener { result ->
        val posts = result.documents.mapNotNull { it.data }
        onSuccess(posts)
    }
    .addOnFailureListener { e ->
        onFailure("Failed to fetch posts: ${e.message}")
    }
    }

    fun updatePostDetails(postId: String, newImageUrl: String?, newTextContent: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val updates = mutableMapOf<String, Any>()

        if (!newImageUrl.isNullOrEmpty()) {
            updates["imageUrl"] = newImageUrl
        }
        if (newTextContent.isNotEmpty()) {
            updates["textContent"] = newTextContent
        }

        if (updates.isNotEmpty()) {
            postsCollection.document(postId)
                .update(updates)
                .addOnSuccessListener {
                    Log.d("FirebaseModel", "Post updated successfully")
                    onSuccess(toString())
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseModel", "Error updating post", e)
                    onError(e.message ?: "Unknown error")
                }
        } else {
            onError("No changes detected")
        }
    }

    fun deleteImageFromCloudinary(imageUrl: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        cloudinaryModel.deleteImageFromCloudinary(imageUrl, onSuccess, onError)
    }
    suspend fun getCommentsForPost(postId: String): List<CommentModel> {
        return try {

            val querySnapshot = firestore.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val comments = querySnapshot.documents.mapNotNull { document ->
                val comment = document.toObject(CommentModel::class.java)?.copy(
                    commentId = document.id,  // ✅ Ensure commentId is set correctly
                    postId = postId           // ✅ Ensure postId is added to each comment
                )
                Log.d("FirestoreDebug", "Fetched Comment: $comment") // ✅ Log fetched comments
                comment
            }


            Log.d("FirestoreDebug", "Total Comments Fetched: ${comments.size}") // ✅ Log the number of comments
            comments
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error fetching comments: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun addComment(postId: String, comment: CommentModel) {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val postRef = firestore.collection("posts").document(postId)

                // Create a new comment document with an auto-generated ID
                val newCommentRef = postRef.collection("comments").document()

                comment.apply {
                    userId = currentUser.uid  // Set the userId from Firebase User
                    commentId = newCommentRef.id  // Set the commentId to the new document ID
                }

                // Add the comment to Firestore
                newCommentRef.set(comment).await()

                // **Increment the comments count atomically**
                postRef.update("commentsCount", FieldValue.increment(1)).await()

                Log.d("FirestoreDebug", "Comment added successfully and count updated.")

            } else {
                Log.e("FirestoreError", "User not logged in")
            }
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error adding comment: ${e.message}", e)
        }
    }



}




















