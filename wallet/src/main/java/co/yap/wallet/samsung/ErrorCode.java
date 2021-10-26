package co.yap.wallet.samsung;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.samsung.android.sdk.samsungpay.v2.AppToAppConstants;
import com.samsung.android.sdk.samsungpay.v2.SpaySdk;
import com.samsung.android.sdk.samsungpay.v2.WatchManager;
import com.samsung.android.sdk.samsungpay.v2.card.CardManager;
import com.samsung.android.sdk.samsungpay.v2.payment.MstManager;
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager;

import java.lang.reflect.Field;

import static com.samsung.android.sdk.samsungpay.v2.SpaySdk.EXTRA_ERROR_REASON;
import static com.samsung.android.sdk.samsungpay.v2.SpaySdk.EXTRA_ERROR_REASON_MESSAGE;

public class ErrorCode {

    private static final String TAG = ErrorCode.class.getSimpleName();
    private static ErrorCode sInstance;
    private SparseArray<String> errorCodeMap = new SparseArray<>();

    private ErrorCode() {
        createErrorCodeMap(SpaySdk.class);
        createErrorCodeMap(PaymentManager.class);
        createErrorCodeMap(CardManager.class);
        createErrorCodeMap(AppToAppConstants.class);
        createErrorCodeMap(MstManager.class);
        createErrorCodeMap(WatchManager.class);
    }

    public static synchronized ErrorCode getInstance() {
        if (sInstance == null) {
            sInstance = new ErrorCode();
        }
        return sInstance;
    }

    private void createErrorCodeMap(Class c) {
        Field[] fields = c.getDeclaredFields();
        for (Field fld : fields) {
            if (fld.getType() == int.class) {
                try {
                    int v = fld.getInt(null);
                    String name = fld.getName();
                    if ((name.startsWith("ERROR_") && v != SpaySdk.ERROR_NONE) || name.startsWith("SPAY_")) {
                        // ERROR_NONE is not an error.
                        errorCodeMap.put(v, name);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "createErrorCodeMap - e : " + e.getMessage());
                }
            }
        }
    }
    public String getSPayError(int status , Bundle bundle){
        StringBuilder builder = new StringBuilder();
        builder.append(getErrorCodeName(status));
        builder.append("\n");
        builder.append(getErrorFromBundle(bundle));
        return  builder.toString();
    }

    private String getErrorCodeName(int i) {
        return errorCodeMap.get(i);
    }

    private String getErrorFromBundle(Bundle bundle) {
        StringBuilder errorString = new StringBuilder();
        for (String key : bundle.keySet()) {
            errorString.append(key);
            errorString.append(":");
            Object bundleObject = bundle.get(key);

            if (bundleObject != null) {
                if (key.equalsIgnoreCase(EXTRA_ERROR_REASON)) {
                    Integer errorNum = (Integer) bundleObject;
                    String errorReasonMsg = bundleObject + ", " + ErrorCode.getInstance().getErrorCodeName(errorNum);
                    errorString.append(errorReasonMsg).append("\n");

                } else if (key.equalsIgnoreCase(EXTRA_ERROR_REASON_MESSAGE)) {
                    errorString.append((String) bundleObject).append("\n");
                } else {
                    errorString.append(bundleObject.toString()).append("\n");
                }
            } else {
                errorString.append("").append("\n");
            }
        }
        return errorString.toString();
    }
}