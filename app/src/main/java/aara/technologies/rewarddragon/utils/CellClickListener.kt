package aara.technologies.rewarddragon.utils

import aara.technologies.rewarddragon.model.AvatarModel

interface CellClickListener {
    fun onCellClickListener(position: Int, model: AvatarModel)

}