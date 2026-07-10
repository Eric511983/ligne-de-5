const sharp = require('sharp');
const path = require('path');

const dir = __dirname;

async function main() {
  const bg = await sharp(path.join(dir, 'icon-background.svg')).png().toBuffer();
  const fg = await sharp(path.join(dir, 'icon-foreground.svg')).png().toBuffer();

  await sharp(bg).toFile(path.join(dir, 'icon-background.png'));
  await sharp(fg).toFile(path.join(dir, 'icon-foreground.png'));

  await sharp(bg)
    .composite([{ input: fg }])
    .png()
    .toFile(path.join(dir, 'icon.png'));

  await sharp(path.join(dir, 'splash.svg'))
    .resize(2732, 2732)
    .png()
    .toFile(path.join(dir, 'splash.png'));

  console.log('Assets générés.');
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
