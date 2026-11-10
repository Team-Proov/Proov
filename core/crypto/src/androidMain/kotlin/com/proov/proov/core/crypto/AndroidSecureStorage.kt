package com.proov.proov.core.crypto

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.io.File

/**
 * 암호문 파일은 `noBackupFilesDir`에 둔다. Keystore 키는 백업으로 복원되지 않으므로
 * 자동 백업에 포함되면 새 기기에서 복호화할 수 없는 암호문만 남는다.
 */
@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class AndroidSecureStorage(context: Context) :
    SecureStorage by FileSecureStorage(File(context.noBackupFilesDir, "secure"), KeystoreAesKeys())
