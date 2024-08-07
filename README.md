# Knokko's Custom Items

This is the source code for the Knokko's Custom Items 
minecraft plug-in: 
 - https://dev.bukkit.org/projects/custom-items-and-textures (BukkitDev)
 - https://www.spigotmc.org/resources/knokkos-custom-items.88182/ (Spigot)
 - https://www.mcbbs.net/forum.php?mod=viewthread&tid=1177493&extra=page%3D1%26filter%3Dsortid%26sortid%3D7%26searchoption%5B61%5D%5Bvalue%5D%3Dknokko%26searchoption%5B61%5D%5Btype%5D%3D (MCBBS) [Chinese] (This page is not made by me)
 - https://polymart.org/resource/knokko-x27-s-custom-items.1190 (Polymart)

This plug-in makes it possible to add custom items with 
their own textures, without sacrificing existing minecraft 
items. (At least, if the (server) resourcepack is used.)
More information about these tricks can be found
[here](docs/custom-texture-system.md).

## Development
Docker is strongly recommended for development of this
plug-in (if you have plenty of disk space). See
[this page](docs/development-with-docker.md) for details.

If you don't like Docker or have finite disk space, you can
follow these 
[more complicated instructions](docs/development-without-docker.md)
instead.

As you may have noticed, this plug-in has a lot of 
modules/gradle projects. See [this page](docs/modules.md)
for an overview.

## Creating releases
Because creating releases is a complicated process when
you support many minecraft versions, there is an automatic
task for this. See [this page](docs/create-release.md)
for the instructions.

## Adding KCI as dependency to your plug-in
If you are a plug-in developer and want to interact with
KCI, you should look [here](docs/add-kci-as-dependency.md).
