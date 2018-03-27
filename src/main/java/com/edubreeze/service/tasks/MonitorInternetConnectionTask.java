package com.edubreeze.service.tasks;

import com.edubreeze.net.ApiClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.io.IOException;
import java.net.InetAddress;

public class MonitorInternetConnectionTask extends ScheduledService<Boolean> {

    private static final SimpleBooleanProperty connectionStatusProperty = new SimpleBooleanProperty();

    @Override
    protected Task<Boolean> createTask() {
        this.setPeriod(Duration.seconds(10));
        this.setRestartOnFailure(true);
        this.setMaximumCumulativePeriod(Duration.minutes(5));

        return new Task<Boolean>() {

            @Override
            protected Boolean call() throws Exception {
                int timeout = 10000; // milliseconds i.e 10 seconds
                boolean isInternetAvailable = connectionStatusProperty.get();

                /**
                 * get all inet address, and assume internet connection is available if at least one is reachable.
                 */
                InetAddress[] addresses = InetAddress.getAllByName(ApiClient.DOMAIN_NAME);
                for (InetAddress address : addresses) {
                    if (isCancelled()) {
                        return isInternetAvailable;
                    }
                    try {
                        if (address.isReachable(timeout)) {
                            isInternetAvailable = true;
                        }
                    } catch (IOException ex) {
                        isInternetAvailable = false;

                    } finally {
                        final boolean currentStatus = isInternetAvailable;
                        Platform.runLater(() -> {
                            connectionStatusProperty.set(currentStatus);
                        });
                    }
                }

                return isInternetAvailable;
            }
        };
    }

    public static SimpleBooleanProperty getConnectionStatusProperty() {
        return connectionStatusProperty;
    }
}
