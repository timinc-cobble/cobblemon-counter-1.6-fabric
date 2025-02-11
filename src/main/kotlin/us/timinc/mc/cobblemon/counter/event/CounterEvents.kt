package us.timinc.mc.cobblemon.counter.event

import com.cobblemon.mod.common.api.reactive.CancelableObservable
import com.cobblemon.mod.common.api.reactive.EventObservable

object CounterEvents {
    @JvmField
    val RECORD_PRE = CancelableObservable<RecordEvent.Pre>()

    @JvmField
    val RECORD_POST = EventObservable<RecordEvent.Post>()
}