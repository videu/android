package club.sandtler.devid.ui;

/*
 * Copyright (c) 2019 Felix Kopp <sandtler@sandtler.club>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import club.sandtler.devid.R;
import club.sandtler.devid.data.LoginDataSource;
import club.sandtler.devid.data.Result;
import club.sandtler.devid.data.model.LoggedInUser;
import club.sandtler.devid.lib.Constants;

/**
 * The login activity screen w/ username and password prompt.
 *
 * This activity may also be launched by the Android system directly
 * when the user tries to add their account from the settings app.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    /**
     * Intent extra key for specifying the account type.
     * This should always be specified.
     */
    public static final String EXTRA_ACCOUNT_TYPE =
            "club.sandtler.devid.ui.LoginActivity.ACCOUNT_TYPE";

    /**
     * Optional Intent extra for specifying an account name
     * (will become the user name).
     */
    public static final String EXTRA_ACCOUNT_NAME =
            "club.sandtler.devid.ui.LoginActivity.ACCOUNT_NAME";

    private AccountManager mAccountMgr;
    private LoginTask mLoginTask;

    private EditText mUserNameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        this.mUserNameEditText = findViewById(R.id.login_user_name);
        this.mPasswordEditText = findViewById(R.id.login_password);
        this.mProgressBar = findViewById(R.id.login_loading);
        this.mLoginButton = findViewById(R.id.login_button);

        this.mAccountMgr = AccountManager.get(this);

        Intent intent = getIntent();
        if (intent.hasExtra(LoginActivity.EXTRA_ACCOUNT_NAME))
            this.mUserNameEditText.setText(intent.getStringExtra(LoginActivity.EXTRA_ACCOUNT_NAME));

        // Update the login button if both username and password input
        // are filled out.
        TextWatcher afterTextChangedListener = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateLoginButton();
            }

        };
        this.mUserNameEditText.addTextChangedListener(afterTextChangedListener);
        this.mPasswordEditText.addTextChangedListener(afterTextChangedListener);

        this.mPasswordEditText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE)
                            handleLogin(mLoginButton);

                        return false;
                    }

                }
        );
    }

    /**
     * onClick callback for the login button.
     * Sends the authentication request.
     *
     * @param v The view.
     */
    public void handleLogin(@Nullable View v) {
        String userName = this.mUserNameEditText.getText().toString();
        String password = this.mPasswordEditText.getText().toString();

        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            showProgress();
            this.mLoginTask = new LoginTask();
            this.mLoginTask.execute(userName, password);
        }
    }

    /**
     * Update the login button's enabled state depending on whether
     * both username and password inputs are filled out.
     */
    private void updateLoginButton() {
        String userName = this.mUserNameEditText.getText().toString().trim();
        String password = this.mPasswordEditText.getText().toString();

        this.mLoginButton.setEnabled(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password));
    }

    /** Show the progress bar. */
    private void showProgress() {
        this.mProgressBar.setVisibility(View.VISIBLE);
    }

    /** Hide the progress bar. */
    private void hideProgress() {
        this.mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Callback for successful authentication results.
     *
     * @param user The logged in user model.
     */
    private void onAuthSuccess(LoggedInUser user) {
        final Account account = new Account(user.getUserName(), Constants.ACCOUNT_TYPE);
        this.mAccountMgr.setAuthToken(account, Constants.AUTH_TOKEN_TYPE, user.getAuthToken());
        this.mAccountMgr.addAccountExplicitly(
                account,
                this.mPasswordEditText.getText().toString(),
                user.toBundle()
        );
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, true);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        String welcomeMsg = String.format(getString(R.string.welcome), user.getDisplayName());
        Toast.makeText(this, welcomeMsg, Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Callback for failed authentication results.
     *
     * @param e The exception that occurred during the authentication process.
     */
    private void onAuthError(Exception e) {
        // TODO: Display error messages
        e.printStackTrace();
    }

    /**
     * Callback for cancelled authentication attempts.
     */
    private void onAuthCancelled() {
        this.mLoginTask = null;
        hideProgress();
    }

    /**
     * Async task for submitting the login credentials to the backend.
     */
    private class LoginTask extends AsyncTask<String, Void, Result<LoggedInUser>> {

        @Override
        protected Result<LoggedInUser> doInBackground(String... params) {
            return new LoginDataSource().loginSync(params[0], params[1]);
        }

        @Override
        public void onPostExecute(final Result<LoggedInUser> result) {
            if (result instanceof Result.Success)
                onAuthSuccess(((Result.Success<LoggedInUser>) result).getData());
            else if (result instanceof Result.Error)
                onAuthError(((Result.Error) result).getError());
        }

        @Override
        protected void onCancelled() {
            onAuthCancelled();
        }

    }

}
