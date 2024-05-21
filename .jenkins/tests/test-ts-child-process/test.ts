import {exec} from 'child_process';


console.log('SUCCESS!!');

exec('echo YEY!!');

const command = `
export NVM_DIR="\\$HOME/.nvm"
[ -s "\\$NVM_DIR/nvm.sh" ] && \\\\. "\\$NVM_DIR/nvm.sh"  # This loads nvm
nvm install
nvm use
npm i -g ts-node@latest`;

new Promise((resolve, reject) => {
	console.log('RUNNING!!');
	exec(command, (error, stdout, stderr) => {
		console.error(error);
		resolve({stdout, stderr});
	});
}).then(({stdout, stderr}: any) => {
	console.log('DONE!!');
	console.log(stdout);
	console.error(stderr);
});

console.log('BYE!!');
