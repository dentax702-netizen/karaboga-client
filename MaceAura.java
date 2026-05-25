{
  "schemaVersion": 1,
  "id": "karaboga",
  "version": "${version}",
  "name": "Karaboga Client",
  "description": "A feature-rich Fabric client mod with beautiful GUI for Minecraft 1.21.11",
  "authors": ["DentaxDev"],
  "contact": {
    "homepage": "https://dentaxclient.com",
    "sources": "https://github.com/dentax/dentax-client"
  },
  "license": "MIT",
  "icon": "assets/dentax/icon.png",
  "environment": "client",
  "entrypoints": {
    "client": [
      "com.dentax.client.DentaxClient"
    ]
  },
  "mixins": [
    "dentax.mixins.json"
  ],
  "depends": {
    "fabricloader": ">=0.18.1",
    "minecraft": "~1.21.11",
    "java": ">=21",
    "fabric-api": "*"
  }
}
