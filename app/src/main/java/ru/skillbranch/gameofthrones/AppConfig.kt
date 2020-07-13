package ru.skillbranch.gameofthrones

object AppConfig {
    val NEED_HOUSES = arrayOf(
        "House Stark of Winterfell",
        "House Lannister of Casterly Rock"
        //FIXME: uncomment
        /*"House Targaryen of King's Landing",
        "House Greyjoy of Pyke",
        "House Tyrell of Highgarden",
        "House Baratheon of Dragonstone",
        "House Nymeros Martell of Sunspear" */
    )
    const val BASE_URL = "https://www.anapioficeandfire.com/"
}

//FIXME: temporary, remake to styles
enum class NobleHouse(
    val fullName: String,
    val shortName: String,
    val colorAccentId: Int,
    val colorPrimaryId: Int,
    val colorDarkId: Int,
    val coatOfArmsId: Int,
    val iconId: Int
) {
    STARK(
        fullName = "House Stark of Winterfell",
        shortName = "Stark",
        colorAccentId = R.color.stark_accent,
        colorPrimaryId = R.color.stark_primary,
        colorDarkId = R.color.stark_dark,
        coatOfArmsId = R.drawable.stark_coast_of_arms,
        iconId = R.drawable.stark_icon
    ),
    LANNISTER(
        fullName = "House Lannister of Casterly Rock",
        shortName = "Lannister",
        colorAccentId = R.color.lannister_accent,
        colorPrimaryId = R.color.lannister_primary,
        colorDarkId = R.color.lannister_dark,
        coatOfArmsId = R.drawable.lannister__coast_of_arms,
        iconId = R.drawable.lanister_icon
    ),
    TARGARYEN(
        fullName = "House Targaryen of King's Landing",
        shortName = "Targaryen",
        colorAccentId = R.color.targaryen_accent,
        colorPrimaryId = R.color.targaryen_primary,
        colorDarkId = R.color.targaryen_dark,
        coatOfArmsId = R.drawable.targaryen_coast_of_arms,
        iconId = R.drawable.targaryen_icon
    ),
    GREYJOY(
        fullName = "House Greyjoy of Pyke",
        shortName = "Greyjoy",
        colorAccentId = R.color.greyjoy_accent,
        colorPrimaryId = R.color.greyjoy_primary,
        colorDarkId = R.color.greyjoy_dark,
        coatOfArmsId = R.drawable.greyjoy_coast_of_arms,
        iconId = R.drawable.greyjoy_icon
    ),
    TYRELL(
        fullName = "House Tyrell of Highgarden",
        shortName = "Tyrell",
        colorAccentId = R.color.tyrel_accent,
        colorPrimaryId = R.color.tyrel_primary,
        colorDarkId = R.color.tyrel_dark,
        coatOfArmsId = R.drawable.tyrel_coast_of_arms,
        iconId = R.drawable.tyrel_icon
    ),
    BARATHEON(
        fullName = "House Baratheon of Dragonstone",
        shortName = "Baratheon",
        colorAccentId = R.color.baratheon_accent,
        colorPrimaryId = R.color.baratheon_primary,
        colorDarkId = R.color.baratheon_dark,
        coatOfArmsId = R.drawable.baratheon_coast_of_arms,
        iconId = R.drawable.baratheon_icon
    ),
    MARTELL(
        fullName = "House Nymeros Martell of Sunspear",
        shortName = "Martell",
        colorAccentId = R.color.martel_accent,
        colorPrimaryId = R.color.martel_primary,
        colorDarkId = R.color.martel_dark,
        coatOfArmsId = R.drawable.martel_coast_of_arms,
        iconId = R.drawable.martel_icon
    );

    companion object {
        fun findByShortName(name: String): NobleHouse? =
            values().firstOrNull { it.shortName == name }
    }
}