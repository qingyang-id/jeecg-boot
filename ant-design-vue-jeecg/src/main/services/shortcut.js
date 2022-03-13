const electronLocalShortcut = require('electron-localshortcut')

export function register(win, key, cb) {
	electronLocalShortcut.register(win, key, () => {
		cb()
	})
}

export function isRegistered(win, key) {
	return electronLocalShortcut.isRegistered(win, key)
}

export function unregister(win, key) {
	electronLocalShortcut.unregister(win, key)
}

export function unregisterAll(win) {
	electronLocalShortcut.unregisterAll(win)
}
