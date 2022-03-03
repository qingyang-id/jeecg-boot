/* global __static */
import path from 'path';
import { app, nativeImage, Tray, Menu } from 'electron';

function createMenuTemplate(win) {
  return [{
      label: '退出',
      icon: nativeImage.createFromPath(
        path.join(__static, 'img/icons/exit.png')
      ),
      accelerator: 'CmdOrCtrl+W',
      click: () => {
        app.exit();
      },
    },
  ];
}

class YPMTrayWindowsImpl {
  constructor(tray, win, emitter) {
    this.tray = tray;
    this.win = win;
    this.emitter = emitter;
    this.template = createMenuTemplate(win);
    this.contextMenu = Menu.buildFromTemplate(this.template);

    this.isPlaying = false;
    this.curDisplayPlaying = false;

    this.isLiked = false;
    this.curDisplayLiked = false;

    this.handleEvents();
  }

  handleEvents() {
    this.tray.on('click', () => {
      this.win.show();
    });

    this.tray.on('right-click', () => {
      if (this.isPlaying !== this.curDisplayPlaying) {
        this.curDisplayPlaying = this.isPlaying;
        this.contextMenu.getMenuItemById('play').visible = !this.isPlaying;
        this.contextMenu.getMenuItemById('pause').visible = this.isPlaying;
      }

      if (this.isLiked !== this.curDisplayLiked) {
        this.curDisplayLiked = this.isLiked;
        this.contextMenu.getMenuItemById('like').visible = !this.isLiked;
        this.contextMenu.getMenuItemById('unlike').visible = this.isLiked;
      }

      this.tray.popUpContextMenu(this.contextMenu);
    });

    this.emitter.on('updateTooltip', title => this.tray.setToolTip(title));
    this.emitter.on(
      'updatePlayState',
      isPlaying => (this.isPlaying = isPlaying)
    );
    this.emitter.on('updateLikeState', isLiked => (this.isLiked = isLiked));
  }
}

export function createTray(win, eventEmitter) {
  let icon = nativeImage
    .createFromPath(path.join(__static, 'img/icons/menu@88.png'))
    .resize({
      height: 20,
      width: 20,
    });

  let tray = new Tray(icon);
  tray.setToolTip('erp-admin');

  return new YPMTrayWindowsImpl(tray, win, eventEmitter);
}
