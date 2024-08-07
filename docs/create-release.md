## Create a release
To create a release, you should simply push all your changes to GitHub, which will cause a
GitHub Actions workflow to produce everything. Unlike the development build, this will
work on all minecraft versions supported by this plug-in. Everything will be put in a
`releases.zip` artifact for the commit, which contains the following files:
- `CustomItems.jar`: the jar file that should be put in the `plugins` folder of the server
- `Editor.jar` the 'raw' Editor: people with a Java installation can double-click it to run
  the Editor.
- `editor-linux.zip`: Linux users without Java installation can download this, extract
  the contents, and double-click `editor` to run the Editor
- `editor-macosx.zip`: Mac users without Java installation should be able to run the
  Editor using this file, but I'm not exactly sure how since I don't have a Mac
- `editor-windows.zip`: Windows users without Java installation can download this,
  extract the contents, and double-click `editor.exe` to run the Editor

The primary advantage of the 'raw' `Editor.jar` over the native editors is that it is significantly smaller since
it doesn't need to bundle a complete JRE.

Note: you should increment the plug-in version in
`shared-code/src/main/resources/plugin.yml` before pushing.

Once you downloaded the `releases.zip`, you can create a
release via the GitHub UI, and publish CustomItems.jar and
Editor.jar on BukkitDev, SpigotMC, and PolyMart.
