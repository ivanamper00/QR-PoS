package com.qr.pos.amper.common.domain.interactor

import com.qr.pos.amper.BuildConfig
import com.qr.pos.amper.common.domain.repository.TimeRepo
import javax.inject.Inject

class GetServerTime @Inject constructor(
    private val repo: TimeRepo
) {
    suspend operator fun invoke() = repo.getServerTime(BuildConfig.TIME_ZONE_PH)
}