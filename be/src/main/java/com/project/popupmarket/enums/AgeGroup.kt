package com.project.popupmarket.enums

enum class AgeGroup(val desc: String) {
    TEEN("10대"),
    TWENTIES("20대"),
    THIRTIES("30대"),
    FORTIES("40대"),
    FIFTIES("50대"),
    SIXTY_PLUS("60대 이상");

    companion object {
        fun fromDisplayName(displayName: String): AgeGroup? {
            return AgeGroup.entries.find { it.desc == displayName }
        }
    }
}