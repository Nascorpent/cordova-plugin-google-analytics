package com.nascorpent;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

public class cordovaanalyticspluginnascorpent extends CordovaPlugin {

    private static final String TAG = "AnalyticsPlugin";

    private FirebaseAnalytics mFirebaseAnalytics;
    private final Semaphore semaphore = new Semaphore(5); // Limite de threads simultâneas
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "Starting Firebase Analytics plugin");
        Context context = this.cordova.getActivity().getApplicationContext();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        // Inicializar o agendamento da fila de tarefas
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                while (!taskQueue.isEmpty()) {
                    cordova.getThreadPool().execute(taskQueue.poll());
                }
            }
        }, 0, 1, TimeUnit.SECONDS); // Executar a cada 1 segundo
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scheduler.shutdown(); // Finalizar o scheduler
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "setUserId":
                this.setUserId(args, callbackContext);
                return true;
            case "setUserProperty":
                this.setUserProperty(args, callbackContext);
                return true;
            case "logEvent":
                this.logEvent(args, callbackContext);
                return true;
            case "setCurrentScreen":
                this.setCurrentScreen(args, callbackContext);
                return true;
            case "resetAnalyticsData":
                this.resetAnalyticsData(callbackContext);
                return true;
            case "setAnalyticsCollectionEnabled":
                this.setAnalyticsCollectionEnabled(args, callbackContext);
                return true;
            case "setDefaultEventParameters":
                this.setDefaultEventParameters(args, callbackContext);
                return true;
            default:
                return false;
        }
    }

    private void setUserId(final JSONArray args, final CallbackContext callbackContext) {
        enqueueTask(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    String userId = args.getString(0);
                    mFirebaseAnalytics.setUserId(userId);
                    callbackContext.success();
                } catch (JSONException | InterruptedException e) {
                    callbackContext.error("setUserId failed: " + e.getMessage());
                } finally {
                    semaphore.release();
                }
            }
        });
    }

    private void setUserProperty(final JSONArray args, final CallbackContext callbackContext) {
        enqueueTask(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    String propertyName = args.getString(0);
                    String propertyValue = args.getString(1);
                    mFirebaseAnalytics.setUserProperty(propertyName, propertyValue);
                    callbackContext.success();
                } catch (JSONException | InterruptedException e) {
                    callbackContext.error("setUserProperty failed: " + e.getMessage());
                } finally {
                    semaphore.release();
                }
            }
        });
    }

    private void logEvent(final JSONArray args,final CallbackContext callbackContext) {
        enqueueTask(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    String eventName = args.getString(0);
                    String eventParam = args.getString(1);

                    Bundle bundle = new Bundle();
                    bundle.putString("parametro", eventParam);

                    mFirebaseAnalytics.logEvent(eventName, bundle);
                    callbackContext.success();
                } catch (JSONException | InterruptedException e) {
                    callbackContext.error("logEvent failed: " + e.getMessage());
                } finally {
                    semaphore.release();
                }
            }
        });
    }


    private void setCurrentScreen(final JSONArray args, final CallbackContext callbackContext) {
        enqueueTask(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    String screenName = args.getString(0);
                    String screenClassOverride = args.optString(1, null);
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
                    if (screenClassOverride != null) {
                        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClassOverride);
                    }

                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
                    callbackContext.success();
                } catch (JSONException | InterruptedException e) {
                    callbackContext.error("setCurrentScreen failed: " + e.getMessage());
                } finally {
                    semaphore.release();
                }
            }
        });
    }

    private void resetAnalyticsData(final CallbackContext callbackContext) {
        enqueueTask(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    mFirebaseAnalytics.resetAnalyticsData();
                    callbackContext.success();
                } catch (InterruptedException e) {
                    callbackContext.error("resetAnalyticsData failed: " + e.getMessage());
                } finally {
                    semaphore.release();
                }
            }
        });
    }

    private void setAnalyticsCollectionEnabled(final JSONArray args, final CallbackContext callbackContext) {
        enqueueTask(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    boolean enabled = args.getBoolean(0);
                    mFirebaseAnalytics.setAnalyticsCollectionEnabled(enabled);
                    callbackContext.success();
                } catch (JSONException | InterruptedException e) {
                    callbackContext.error("setAnalyticsCollectionEnabled failed: " + e.getMessage());
                } finally {
                    semaphore.release();
                }
            }
        });
    }

    private void setDefaultEventParameters(final JSONArray args, final CallbackContext callbackContext) {
        enqueueTask(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    JSONObject params = args.getJSONObject(0);
                    Bundle bundle = new Bundle();

                    Iterator<String> iter = params.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        Object value = params.get(key);

                        if (value instanceof String) {
                            bundle.putString(key, (String) value);
                        } else if (value instanceof Integer) {
                            bundle.putInt(key, (Integer) value);
                        } else if (value instanceof Double) {
                            bundle.putDouble(key, (Double) value);
                        } else if (value instanceof Float) {
                            bundle.putFloat(key, (Float) value);
                        } else if (value instanceof Long) {
                            bundle.putLong(key, (Long) value);
                        } else if (value instanceof Boolean) {
                            bundle.putBoolean(key, (Boolean) value);
                        } else {
                            Log.w(TAG, "Ignoring unsupported type: " + value.getClass().getName());
                            callbackContext.error("Unsupported event parameter type: " + value.getClass().getName());
                            return;
                        }
                    }

                    mFirebaseAnalytics.setDefaultEventParameters(bundle);
                    callbackContext.success();
                } catch (JSONException | InterruptedException e) {
                    callbackContext.error("setDefaultEventParameters failed: " + e.getMessage());
                } finally {
                    semaphore.release();
                }
            }
        });
    }

    private void enqueueTask(Runnable task) {
        taskQueue.offer(task);
    }
}
