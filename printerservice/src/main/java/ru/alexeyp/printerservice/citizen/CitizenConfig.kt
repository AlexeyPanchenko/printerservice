package ru.alexeyp.printerservice.citizen

object CitizenConfig {

    object Alignment {
        const val LEFT = 0
        const val CENTER = 1
        const val RIGHT = 2
    }

    object Font {
        const val DEFAULT = 0
        const val FONTB = 1
        const val FONTC = 2
        const val BOLD = 8
        const val REVERSE = 16
        const val UNDERLINE = 128
        const val ITALIC = 256
    }

    object Width {
        const val WIDTH1 = 0
        const val WIDTH2 = 16
        const val WIDTH3 = 32
        const val WIDTH4 = 48
        const val WIDTH5 = 64
        const val WIDTH6 = 80
        const val WIDTH7 = 96
        const val WIDTH8 = 112
    }

    object Height {
        const val HEIGHT1 = 0
        const val HEIGHT2 = 1
        const val HEIGHT3 = 2
        const val HEIGHT4 = 3
        const val HEIGHT5 = 4
        const val HEIGHT6 = 5
        const val HEIGHT7 = 6
        const val HEIGHT8 = 7
    }
}