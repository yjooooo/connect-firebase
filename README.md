# 💾 How to use the <u>Firebase Realtime database</u> in an Android project.

### 🍀Step 1. Create Firebase Project

<div>
  <img width="400" alt="스크린샷 2021-02-23 오후 2 32 52" src="https://user-images.githubusercontent.com/68374234/108806229-74f2e200-75e4-11eb-9b26-b91ba967e8e6.png">
  <img width="400" alt="스크린샷 2021-02-23 오후 3 26 43" src="https://user-images.githubusercontent.com/68374234/108809192-a7540d80-75eb-11eb-9377-aec97f5bb000.png">
</div>



### 🍀Step 2. Add Firebase to Android App

<div>
  <img width="1000" alt="step2" src="https://user-images.githubusercontent.com/68374234/108811727-1c761180-75f1-11eb-902a-f4bf6fb23768.png">
</div>
<div>
  <img width="1000" alt="step2" src="https://user-images.githubusercontent.com/68374234/108813008-7c6db780-75f3-11eb-8373-d52310e0516e.png">
</div>
<div>
<img width="1000" alt="step2" src="https://user-images.githubusercontent.com/68374234/108814500-441ba880-75f6-11eb-8403-821f2fe3f5c7.png">
</div>



### 🍀Step 3. Add Internet Permission to AndroidMenifest.xml

<div>
  <img width="1000" alt="step3" src="https://user-images.githubusercontent.com/68374234/108815673-2d765100-75f8-11eb-9be0-58da5b18ad5c.png">
</div>



### 🍀Step 4. Create Realtime Database And Setting database rules

- Since it is for testing, set both read and write to true.

<div>
  <img width="1000" alt="step2" src="https://user-images.githubusercontent.com/68374234/108809666-aff91380-75ec-11eb-958e-b7579fe66875.png">
</div>




# 💾 How to use the <u>Firebase Storage</u> in an Android project.

## 📍Upload File (using URI)

### 🍀Step 1. Add Cloud Storage SDK

```groovy
dependencies {
    // Import the BoM for the Firebase platform
    implementation platform('com.google.firebase:firebase-bom:26.7.0')

    // Declare the dependency for the Cloud Storage library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation 'com.google.firebase:firebase-storage-ktx'
}
```



### 🍀Step 2. Get FirebaseStorage Instance 

```kotlin
private var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
```



### 🍀Step 3. Upload File Using Uri

- 1st child : the folder under storage
- 2nd child : the file under 1st child folder

```kotlin
private fun uploadAudioUri(file: Uri) {
    firebaseStorage.reference.child(filePath).child(fileName + ".mp4")
        .putFile(file).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("storage", "upload success")
            }
        }
}
```



### 🍀Step 3-1. Then How to Get Uri From File?

- Use startActivityForResult() and onActivityResult()
- I upload the file that selected from MediaStore. So, put the uri of the file I selected in Intent.

```kotlin
private fun setOnBtnAudioUploadClick() {
    binding.btnAudioUpload.setOnClickListener {
        val audioIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(audioIntent, 2)
    }
}
```



## 📍Download File 

### 🍀Step 1. Get Firebase StorageReference

```kotlin
pathReference = firebaseStorage.reference.child(filePath).child("$fileName.mp4")
```



### 🍀Step 2. Create A Temporary File And Download File There

- Since the temporary file was created, it should be deleted after use.
- Delete files using deleteOnExit()
  -  Automatically delete saved files when JVM terminates, rather than deleting files immediately.

```kotlin
val localFile = File.createTempFile("temp_download", "mp4")
localFile.deleteOnExit()

pathReference.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
        }.addOnFailureListener {
            // Handle any errors
        }
```

