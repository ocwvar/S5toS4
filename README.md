# SDVX 5 Music Data Converter
## Requirement
2DXBuild(wav -> 2dx):   https://github.com/mon/2dxTools

2dxWavConvert(mp3 -> wav):  https://github.com/mon/2dxTools

ffmpeg(wma -> mp3): https://www.ffmpeg.org/download.html#build-windows

ifs_layeredfs: https://github.com/mon/ifs_layeredfs

AND SDVX 5 MUSIC DATA

## Usage
- 1、Folder structure you should make one like this:


        X:\convert
                \result   (MUST BE EMPTY BEFORE YOU RUN)
                \source   (MUST BE EMPTY BEFORE YOU RUN)
                \tools
                      \2dx    (PUT YOUR "2dxBuild.exe" HERE)
                      \ff     (PUT YOUR "ffmpeg.exe" HERE)
                      \wav    (PUT YOUR ALL "2dxWavConvert'dll and exe" HERE )


- 2、Put your **"sdvx5\content\data\others\music_db.xml"** To **"\source"**

- 3、Put your **"sdvx5\content\data\music"** To **"\source"**

example:


    X:\convert\source\music_db.xml  (FILE)
    X:\convert\source\0168_nanaironouta_uemurakatsuki   (FOLDER)
    

- 4、after you finish download things, you should run


    com.ocwvar.tools.Main                    

with a **folder path as arg[0]**. (maybe i will upload a JAR)

- 5、Get all folders from **"\result"** to your sdvx4 content's **ifs data folder**

example:


    X:\convert\result\1410  -->  X:\sdvx4\content\data_mods\1410
    X:\convert\result\1411  -->  X:\sdvx4\content\data_mods\1411
    X:\convert\result\1412  -->  X:\sdvx4\content\data_mods\1412
    
    
- FIRE UP YOUR GAME

## Issues
Some sdvx5 music data is not compatible with sdvx4, in those music you will lost some VOL slider,
 and you can not finish that game.
 
 example:
 
 
    1420_intothemadness_yutaimai
