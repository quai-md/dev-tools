import {exec} from 'child_process';


console.log('SUCCESS!!');

exec('echo YEY!!');

const command = `
echo 1
export NVM_DIR="\\$HOME/.nvm"
echo 2
[ -s "\\$NVM_DIR/nvm.sh" ] && \\\\. "\\$NVM_DIR/nvm.sh"  # This loads nvm
echo 3
nvm install
echo 4
nvm use
echo 5
npm i -g ts-node@latest
echo 6
`;

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
