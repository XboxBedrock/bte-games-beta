import json
import os

barrierBlockState = {
                "variants": {
                  "": {
                    "model": "minecraft:block/barrier"
                  }
                }
              }

barrierItem = {
    "parent": "minecraft:item/generated",
    "textures": {
        "layer0": "minecraft:item/barrier"
    }
}

barrierBlock = {
    "parent": "minecraft:block/cube_all",
    "textures": {
        "all": "minecraft:block/barrier"
    }
}

import glob

itemModels = glob.glob("./assets/minecraft/models/item" + '/*.json', recursive=True)



def inTrueName(l, n):
    if len(n) < 9:
        return n in l
    for i in l:
        if n in i:
            return True
    return False


with open("../src/main/resources/112.txt") as blocks:
    lines = blocks.readlines()

    newLines = []

    for i in lines:
        i = i.replace('\n', "")
        newLines.append(i)

    for i in itemModels:
        trueName = i.split("/")[-1].replace(".json", "")
        if (not inTrueName(newLines, trueName)):
            with open(i, "w") as f:
                json.dump(barrierItem, f)
        else:
            try:
                print("yes")
                os.remove(i)
            except:
                print(i)


