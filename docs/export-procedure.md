## Exporting
The export procedure starts when the user clicks an `Export...` button in the Editor. The Editor shall ask the user
how the resource pack should be hosted, and give 3 options:
- Automatic: The resource pack will be hosted automatically, and be downloaded by players automatically.
- Manual: The user is responsible for hosting the resource pack and ensuring that players download it.
- Mixed: The user needs to move the resource pack to `plugins/CustomItems`, after which the plug-in will
do the rest of the work.

### Automatic
If the user chooses option (Automatic), the Editor shall 
1. generate the resource pack
2. add the .cis.txt file to the root directory of the resource pack
3. upload it to my resource pack host server
4. and show the command `/kci reload $hash$`, which the user can copy. 

When the user (pastes and) runs this command on his server, the plug-in shall
1. download the resource pack from my resource pack host server
2. extract the .cis.txt file from it
3. save the .cis.txt file to plugins/CustomItems/items.cis.txt
4. save the rest of the resource pack to plugins/CustomItems/resource-pack.zip
5. upload the rest of the resource pack to my resource pack host server 
(do **not** include the .cis.txt file because it would allow players 
to use the entire item set on their own server, which some users would not like)

### Manual
If the user chooses option (Manual), the Editor shall 
1. generate the resource pack and the items.cis.txt file
2. and save both of them to the Custom Item Sets folder 
on the computer of the user.

Then the user needs to
1. copy the items.cis.txt file to plugins/CustomItems
2. make sure that the resource pack is somehow downloaded
by the players (or not if he doesn't want to use custom textures)
3. start the server or use `/kci reload`

### Mixed
If the user chooses option (Mixed), the Editor shall
1. generate the resource pack and the items.cis.txt file
2. and save both of them to the Custom Item Sets folder
   on the computer of the user.

Then the user needs to
1. copy the items.cis.txt and resource-pack.zip files to plugins/CustomItems,
possibly after modifying resource-pack.zip manually
2. start the server or use `/kci reload`

## Plug-in responsibilities
### Start-up
Upon start-up, the plug-in shall look for 
the following files in plugins/CustomItems:
- `items.cis.txt`: If this file exists, it shall load the item
set from this file. If not, it shall initialize an empty item set,
which will cause the plug-in to be mostly idle. Furthermore, the
plug-in shall **not** save or load the `gamedata.bin` file.
- `resource-pack.zip`: If this file exists, it shall ensure that this
resource pack is present on my resource pack host server (sending it
if needed), and ensure that players download it. Furthermore, the
plug-in shall prevent my resource pack host from deleting it by
checking its status every 25 minutes. If this file is
missing, the plug-in simply won't try to host the resource pack.

### When a player joins the server
If the file `plugins/CustomItems/resource-pack.zip` existed
when the plug-in was reloaded for the last time (or when the
server started if the plug-in was never reloaded), the plug-in
shall use the Bukkit API to prompt all players to download the
resource pack when they join the server.

### Reload
There are 2 commands that reload the plug-in:
####1. /kci reload $hash$
where the $hash$ is the SHA-256 hash of a
resource pack **including an items.cis.txt file** that is stored 
on my resource pack host server. Upon executing this command,
the plug-in shall:
1. Save the current state to `gamedata.bin` if the old item set
is not empty.
2. Execute all the plug-in responsibilities outlined in
**Export: Option 1**.
3. Load the current state from `gamedata.bin` if the file exists.
4. If the SHA-256 hash of the old resource pack **without**
items.cis.txt is **not** equal to the SHA-256 hash of the new
resource pack **without** items.cis.txt, the plug-in shall
tell all players that they can log out and back in to get the
new resource pack.

#### 2. /kci reload
1. Save the current state to `gamedata.bin` if the (old) item set
   is not empty.
2. Load the new item set from `plugins/CustomItems/items.cis.txt`.
If this file doesn't exist, continue with an empty item set instead.
3. Load the current state from `gamedata.bin` if the file exists
and the new item set is not empty.
4. If there is a resource pack stored at 
`plugins/CustomItems/resource-pack.zip`, the plug-in shall
compute its SHA-256 hash and compare it with the SHA-256 hash
of the old resource-pack. If they are different (or there is no
previous resource pack), the plug-in shall upload the new
resource pack to my resource pack host server, tell all online
players to download it, and ping it every 25 minutes to keep
it on my resource pack host server. If there is no new
resource pack and there used to be a resource pack, the plug-in
should tell all online players that they can disable the
outdated resource pack by logging out and joining again.
