package com.braintreepayments.demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.OnlineCareUS_Pt.OnlineCare;
import com.app.OnlineCareUS_Pt.R;
import com.app.OnlineCareUS_Pt.util.DATA;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.demo.models.Transaction;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateTransactionActivity extends AppCompatActivity {

    public static final String EXTRA_PAYMENT_METHOD_NONCE = "nonce";

    private ProgressBar mLoadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.braintree_create_transaction_activity);
        mLoadingSpinner = findViewById(R.id.loading_spinner);
        setTitle(R.string.processing_transaction);

        sendNonceToServer((PaymentMethodNonce) getIntent().getParcelableExtra(EXTRA_PAYMENT_METHOD_NONCE));
    }

    private void sendNonceToServer(PaymentMethodNonce nonce) {
        Callback<Transaction> callback = new Callback<Transaction>() {
            @Override
            public void success(Transaction transaction, Response response) {
                if (transaction.getMessage() != null &&
                        transaction.getMessage().startsWith("created")) {
                    setStatus(R.string.transaction_complete);
                    setMessage(transaction.getMessage());
                } else {
                    setStatus(R.string.transaction_failed);
                    if (TextUtils.isEmpty(transaction.getMessage())) {
                        setMessage("Server response was empty or malformed");
                    } else {
                        setMessage(transaction.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                setStatus(R.string.transaction_failed);
                setMessage("Unable to create a transaction. Response Code: " +
                        error.getResponse().getStatus() + " Response body: " +
                        error.getResponse().getBody());
            }
        };

        if (Settings.isThreeDSecureEnabled(this) && Settings.isThreeDSecureRequired(this)) {
            OnlineCare.getApiClient(this).createTransaction(nonce.getNonce(),
                    Settings.getThreeDSecureMerchantAccountId(this), true, callback);
        } else if (Settings.isThreeDSecureEnabled(this)) {
            OnlineCare.getApiClient(this).createTransaction(nonce.getNonce(),
                    Settings.getThreeDSecureMerchantAccountId(this), callback);
        } else if (nonce instanceof CardNonce && ((CardNonce) nonce).getCardType().equals("UnionPay")) {
            OnlineCare.getApiClient(this).createTransaction(nonce.getNonce(),
                    Settings.getUnionPayMerchantAccountId(this), callback);
        } else {
            DATA.print("-- merchant account id in create transaction: "+Settings.getMerchantAccountId(this));
            OnlineCare.getApiClient(this).createTransaction(nonce.getNonce(), Settings.getMerchantAccountId(this),
                    callback);
        }
    }

    private void setStatus(int message) {
        mLoadingSpinner.setVisibility(View.GONE);
        setTitle(message);
        TextView status = findViewById(R.id.transaction_status);
        status.setText(message);
        status.setVisibility(View.VISIBLE);
    }

    private void setMessage(String message) {
        mLoadingSpinner.setVisibility(View.GONE);
        TextView textView = findViewById(R.id.transaction_message);
        textView.setText(message);
        textView.setVisibility(View.VISIBLE);
    }
}
