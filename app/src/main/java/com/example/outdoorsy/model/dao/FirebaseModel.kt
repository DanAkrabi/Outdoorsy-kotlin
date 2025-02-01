
package com.example.outdoorsy.model.dao

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import kotlin.math.max

class FirebaseModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,

//    private val userViewModel: UserViewModel
) {
//    private val auth = FirebaseAuth.getInstance()

    private val database =firestore // Initializing a firebase instance
    private val usersCollection = firestore.collection("users")
    private val postsCollection = firestore.collection("posts")

//    private val storage = Firebase.storage

//    init {
//        val setting = firestoreSettings {
//            setLocalCacheSettings(memoryCacheSettings {
//
//            })
//        }
//        database.firestoreSettings = setting
//    }
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
        return try {
            database.collection("users")
                .orderBy("fullname")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()
                .toObjects(UserModel::class.java)
        } catch (e: Exception) {
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
    suspend fun saveUser(user: UserModel): Boolean {
        return try {
            val json = mapOf(
                "email" to user.email,
                "fullname" to user.fullname,
                "password" to user.password,
                "id" to user.id
            )
            usersCollection.document(user.id).set(json).await()
            Log.d("FirebaseModel", "User added with ID: ${user.id}")
            true
        } catch (e: Exception) {
            Log.e("FirebaseModel", "Error adding user", e)
            false
        }
    }

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
            // Register the user using Firebase Authentication
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            // Save additional user information to Firestore
            val userId = firebaseAuth.currentUser?.uid ?: UUID.randomUUID().toString()
            val user = UserModel(
                id = userId,
                email = email,
                fullname = fullname,
                password = password // Consider hashing or encrypting the password in a production app
            )
            saveUser(user)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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



suspend fun getCommentsForPost(postId: String): List<CommentModel> {
    return try {
        val querySnapshot = firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        val comments = querySnapshot.documents.mapNotNull { document ->
            val comment = document.toObject(CommentModel::class.java)
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
            val followingIds = firestore.collection("users")
                .document(userId)
                .collection("following")
                .get()
                .await()
                .documents.mapNotNull { it.id }

            Log.d("FirebaseModel", "User follows: $followingIds")

            if (followingIds.isEmpty()) {
                Log.d("FirebaseModel", "User is not following anyone.")
                return emptyList()
            }

            // Create a compound query to fetch posts from all followed users
            val query = firestore.collection("posts")
                .whereIn("userId", followingIds) // Use whereIn to query across multiple users
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10) // Limit to the newest 10 posts

            val querySnapshot = query.get().await()

            val posts = querySnapshot.documents.mapNotNull { document ->
                document.toObject(PostModel::class.java)?.copy(postId = document.id)
            }

            Log.d("FirebaseModel", "Total Feed posts: ${posts.size}")
            return posts
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


//


//    suspend fun toggleLike(postId: String, userId: String, callback: (Boolean) -> Unit) {
//        val postRef = firestore.collection("posts").document(postId)
//        val likeRef = postRef.collection("likes").document(userId)
//
//        firestore.runTransaction { transaction ->
//            val postSnapshot = transaction.get(postRef)
//            val likeSnapshot = transaction.get(likeRef)
//            var liked = false
//
//            if (likeSnapshot.exists()) {
//                // If the like document exists, the user is unliking the post
//                transaction.delete(likeRef)
//                transaction.update(postRef, "likesCount", FieldValue.increment(-1))
//            } else {
//                // If the like document does not exist, the user is liking the post
//                transaction.set(likeRef, hashMapOf("timestamp" to FieldValue.serverTimestamp()))
//                transaction.update(postRef, "likesCount", FieldValue.increment(1))
//                liked = true
//            }
//            // The operation is atomic; this block either succeeds fully or fails fully.
//            liked // Return the status of the operation to indicate like or unlike
//        }.addOnSuccessListener {
//            callback(true) // Operation succeeded, callback with the result
//        }.addOnFailureListener { e ->
//            Log.e("FirestoreError", "Error toggling like", e)
//            callback(false) // Operation failed, callback with the result
//        }
//    }
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
        val likeRef = postsCollection.document(postId).collection("likes").document(userId)
        return try {
            val snapshot = likeRef.get().await()
            snapshot.exists()
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error checking like status: ${e.message}")
            false
        }
//
    }

}

















//    }

// Upload image to Firebase Storage
//    fun uploadImage(image: Bitmap, name: String, callback: (String?) -> Unit) {
//        val storageRef = storage.reference
//        val imageRef = storageRef.child("images/$name.jpg")
//
//        val baos = ByteArrayOutputStream()
//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()
//
//        val uploadTask = imageRef.putBytes(data)
//        uploadTask.addOnFailureListener {
//            callback(null) // TODO - still need to implement this
//        }.addOnSuccessListener { taskSnapshot ->
//            imageRef.downloadUrl.addOnSuccessListener { uri ->
//                callback(uri.toString())
//            }
//        }
//    }
