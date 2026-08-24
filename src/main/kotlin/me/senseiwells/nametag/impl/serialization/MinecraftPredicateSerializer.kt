package me.senseiwells.nametag.impl.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.util.Identifier
import me.senseiwells.nametag.impl.predicate.MinecraftPredicate

object MinecraftPredicateSerializer : KSerializer<MinecraftPredicate> {
    override val descriptor: SerialDescriptor = ResourceLocationSerializer.descriptor

    override fun serialize(encoder: Encoder, value: MinecraftPredicate) {
        ResourceLocationSerializer.serialize(encoder, value.id)
    }

    override fun deserialize(decoder: Decoder): MinecraftPredicate {
        val id = ResourceLocationSerializer.deserialize(decoder)
        return MinecraftPredicate.get(id) ?: throw IllegalArgumentException("Unknown predicate id: $id")
    }
}
