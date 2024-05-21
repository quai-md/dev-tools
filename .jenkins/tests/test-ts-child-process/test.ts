import {exec} from 'child_process';


console.log('SUCCESS!!');

exec('echo YEY!!');
exec(`
export NVM_DIR="\\$HOME/.nvm"
[ -s "\\$NVM_DIR/nvm.sh" ] && \\\\. "\\$NVM_DIR/nvm.sh"  # This loads nvm
nvm install
nvm use
npm i -g ts-node@latest`, {shell: '/bin/bash'});