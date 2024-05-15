## 6.0.2

### Added

- Added support for custom buttons

Buttons can be added by adding the following to the mods config file. You can have up to 2 buttons. Each button can have a label up to 32 characters long.

If you do not want to have buttons, you can leave the buttons array empty.

```json
{
  "buttons": [
    {
      "label": "Google",
      "url": "https://www.google.com"
    },
    {
      "label": "Yahoo",
      "url": "https://www.yahoo.com"
    }
  ]
}
```

### Fixed

- Fixed the borked reimplementation of KubeJS support. This should now work correctly.
