package com.br.alura.galeria.mlkit

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import javax.inject.Inject

class ImageClassifier @Inject constructor(private val context: Context) {


    fun classifyImage(
        imageUri: String,
        onSucess: (List<String>) -> Unit,
        onFail: () -> Unit
    ) {
        val image = InputImage.fromFilePath(context, Uri.parse(imageUri))

        /*val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.7f)
            .build()*/


        val customModel = LocalModel.Builder()
            .setAssetFilePath("custom_model_73_epoch.tflite")
            .build()

        val customOptions = CustomImageLabelerOptions.Builder(customModel)
            .setConfidenceThreshold(0.2f)
            .build()

        val labeler = ImageLabeling.getClient(customOptions)
        //val labeler = ImageLabeling.getClient(options)
        //val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image).addOnSuccessListener { labels ->

            /*val newLabels = context.resources.assets.open("labels.txt")
                .bufferedReader()
                .readLines()*/

            labels.forEach {
                val labelAndConfidence = "${it.text.substring(2)} - ${it.confidence}"
                Log.d("ImageDetailScreen", labelAndConfidence)
            }
            onSucess(labels.map {it.text.substring(2)})

        }.addOnFailureListener { onFail() }
            .addOnCompleteListener{
                labeler.close()
            }
    }
    /*
        Essa abordagem garante que, no caso de sucesso da análise, todas operações assíncronas devem ser concluídas antes que imageUriManager.setImageWithLabels(imagesWithLabels) seja chamado. Para isso, utilizamos o método awaitAll() em alguns objetos Deferred

    private fun classifyImagesAsync() {
        viewModelScope.launch {
            val imageUris = imageUriManager.getImageUris()
            val imagesWithLabelsDeferred = mutableListOf<Deferred<ImageWithLabels>>()

            imageUris.forEach { imageUri ->
                val deferred = CompletableDeferred<ImageWithLabels>()
                imageClassifier.classifyImage(
                    imageUri = Uri.parse(imageUri),
                    onSuccessful = { labels ->
                        deferred.complete(ImageWithLabels(uri = imageUri, labels = labels))
                    }
                )
                imagesWithLabelsDeferred.add(deferred)
            }

            val imagesWithLabels = imagesWithLabelsDeferred.awaitAll()

            imageUriManager.setImageWithLabels(imagesWithLabels)
            _uiState.value = _uiState.value.copy(
                appState = AppState.Loaded,
                route = Route.GALLERY
            )
        }
    }
     */

}