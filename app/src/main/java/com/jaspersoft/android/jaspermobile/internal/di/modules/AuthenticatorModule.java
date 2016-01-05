package com.jaspersoft.android.jaspermobile.internal.di.modules;

import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.SaveProfileUseCase;
import com.jaspersoft.android.jaspermobile.domain.repository.CredentialsRepository;
import com.jaspersoft.android.jaspermobile.domain.repository.JasperServerRepository;
import com.jaspersoft.android.jaspermobile.domain.repository.ProfileRepository;
import com.jaspersoft.android.jaspermobile.domain.validator.CredentialsValidator;
import com.jaspersoft.android.jaspermobile.domain.validator.ProfileValidator;
import com.jaspersoft.android.jaspermobile.domain.validator.ServerValidator;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.presentation.action.ProfileActionListener;
import com.jaspersoft.android.jaspermobile.presentation.presenter.AuthenticationPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Module
public final class AuthenticatorModule {
    @PerActivity
    @Provides
    ProfileActionListener provideActionListener(AuthenticationPresenter presenter) {
        return presenter;
    }

    @PerActivity
    @Provides
    SaveProfileUseCase provideSaveProfileUseCase(
            PreExecutionThread preExecutionThread,
            PostExecutionThread postExecutionThread,
            ProfileRepository profileRepository,
            JasperServerRepository jasperServerRepository,
            CredentialsRepository credentialsDataRepository,
            ProfileValidator profileValidator,
            ServerValidator serverValidator,
            CredentialsValidator credentialsValidator) {
        return new SaveProfileUseCase(
                preExecutionThread,
                postExecutionThread,
                profileRepository,
                jasperServerRepository,
                credentialsDataRepository,
                profileValidator,
                serverValidator,
                credentialsValidator);
    }
}
