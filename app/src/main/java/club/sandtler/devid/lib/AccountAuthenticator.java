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

package club.sandtler.devid.lib;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import club.sandtler.devid.R;
import club.sandtler.devid.data.LoginDataSource;
import club.sandtler.devid.data.LoginRepository;
import club.sandtler.devid.data.Result;
import club.sandtler.devid.data.model.LoggedInUser;
import club.sandtler.devid.ui.LoginActivity;

/**
 * The account authenticator for DEvid accounts.
 */
public final class AccountAuthenticator extends AbstractAccountAuthenticator {

    /** The context. */
    private Context mContext;

    /**
     * Create a new account authenticator for DEvid user accounts.
     *
     * @param context The (service) context to run the authenticator in.
     */
    public AccountAuthenticator(Context context) {
        super(context);
        this.mContext = context;
    }

    /** {@inheritDoc} */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options) {
        final Intent intent = new Intent(this.mContext, LoginActivity.class);
        intent.putExtra(
                LoginActivity.EXTRA_ACCOUNT_TYPE,
                this.mContext.getString(R.string.account_type)
        );
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    /** {@inheritDoc} */
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     Account account, Bundle options) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse res, String name) {
        // Editing accounts is not supported right now
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options) {
        // TODO: Don't store the password on the device
        final AccountManager am = AccountManager.get(this.mContext);
        final String password = am.getPassword(account);
        if (password != null) {
            Result<LoggedInUser> result = LoginRepository.getInstance(new LoginDataSource())
                    .login(account.name, password);

            if (result instanceof Result.Success) {
                LoggedInUser user = ((Result.Success<LoggedInUser>) result).getData();
                Bundle bundle = new Bundle();
                bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
                bundle.putString(AccountManager.KEY_AUTHTOKEN, user.getAuthToken());
                return bundle;
            }
        }

        final Intent intent = new Intent(this.mContext, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
        intent.putExtra(LoginActivity.EXTRA_ACCOUNT_NAME, account.name);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    /** {@inheritDoc} */
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account, String[] features) {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return bundle;
    }

    /** @inheritDoc */
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                    String authTokenType, Bundle options) {
        throw new UnsupportedOperationException();
    }


}
