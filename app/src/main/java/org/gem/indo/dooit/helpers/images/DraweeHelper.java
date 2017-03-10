package org.gem.indo.dooit.helpers.images;

import android.net.Uri;

import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by Wimpie Victor on 2016/12/15.
 */

public class DraweeHelper {

    public static void setProgressiveUri(SimpleDraweeView imageView, Uri uri) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setRequestPriority(Priority.HIGH)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(imageView.getController())
                .setImageRequest(request)
                .build();
        imageView.setController(controller);
    }

    public static void cacheImage(Uri uri){
        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        if(!imagePipeline.isInBitmapMemoryCache(uri)){
            if(imagePipeline.isInDiskCache(uri).getResult() != null){
                if(!imagePipeline.isInDiskCache(uri).getResult().equals(true)){
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                            .build();
                    imagePipeline.prefetchToBitmapCache(request, null);
                    imagePipeline.prefetchToDiskCache(request, null);
                }
            }
        }
    }
}
