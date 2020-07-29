package com.sparkle.roam.AWS;

import android.content.Context;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.PersistentMutationsCallback;
import com.amazonaws.mobileconnectors.appsync.PersistentMutationsError;
import com.amazonaws.mobileconnectors.appsync.PersistentMutationsResponse;


public class ClientFactory {

    private static volatile AWSAppSyncClient client;

    public synchronized static AWSAppSyncClient getInstance(Context context) {
        if (client == null) {
            AWSConfiguration awsConfig = new AWSConfiguration(context);
            client = AWSAppSyncClient.builder()
                    .context(context)
                    .awsConfiguration(awsConfig)
                    .persistentMutationsCallback(new PersistentMutationsCallback() {
                        @Override
                        public void onResponse(PersistentMutationsResponse response) {

                        }

                        @Override
                        public void onFailure(PersistentMutationsError error) {
                            // handle error feedback here
                        }
                    })
                    .build();
        }
        return client;
    }
}
