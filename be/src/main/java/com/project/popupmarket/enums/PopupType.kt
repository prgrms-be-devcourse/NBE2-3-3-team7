package com.project.popupmarket.enums

enum class PopupType(val desc: String) {
    FOOD("식품"),
    COSMETICS("화장품"),
    FASHION("패션"),
    ANIME("애니"),
    IDOL("아이돌"),
    SPORTS("스포츠"),
    MUSIC("음악"),
    TECH("테크"),
    INTERIOR("인테리어"),
    CULTURE_ART("문화/예술"),
    GAME("게임"),
    HEALTH("헬스"),
    DRINKS("음료"),
    BOOK("책"),
    ECO_FRIENDLY("친환경");

    companion object {
        fun fromDisplayName(displayName: String): PopupType? {
            return entries.find { it.desc == displayName }
        }
    }
}