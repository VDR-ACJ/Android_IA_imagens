package com.br.alura.galeria.mlkit

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import javax.inject.Inject

class ImageClassifier @Inject constructor(private val context: Context) {

    fun classifyImage(
        imageUri: String,
        onSucess: (List<String>) -> Unit,
        onFail: () -> Unit
    ) {
        val image = InputImage.fromFilePath(context, Uri.parse(imageUri))

        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image).addOnSuccessListener { labels ->
            labels.forEach {
                val labelAndConfidence = "${it.text} - ${it.confidence}"
                Log.d("ImageDetailScreen", labelAndConfidence)
            }
            onSucess(labels.map { it.text })

        }.addOnFailureListener { onFail() }
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