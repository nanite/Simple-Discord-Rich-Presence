## 87.0.0
Update to 1.21

## 86.0.1

### Changed

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
