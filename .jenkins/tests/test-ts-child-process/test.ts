import {exec} from 'child_process';


console.log('SUCCESS!!');

exec('echo YEY!!');

const command = `
echo 1
export NVM_DIR="\$HOME/.nvm"
echo "2 export NVM_DIR="\$HOME/.nvm" => $?"

[ -s "\$NVM_DIR/nvm.sh" ] && . "\$NVM_DIR/nvm.sh"  # This loads nvm
echo "3 [ -s "\$NVM_DIR/nvm.sh" ] && . "\$NVM_DIR/nvm.sh" => $?"

nvm install
echo "4 nvm install => $?"

nvm use
echo "5 nvm use => $?"

npm i -g ts-node@latest
echo "6 npm i -g ts-node@latest => $?"
`;

new Promise((resolve, reject) => {
	console.log('RUNNING!!');
	exec(command, (error, stdout, stderr) => {
		console.log('COMPLETED RUNNING!!');
		if (error)
			console.error(error);
		resolve({stdout, stderr});
	});
}).then(({stdout, stderr}: any) => {
	console.log('DONE!!');
	console.log(stdout);

	console.error(stderr);
});

console.log('BYE!!');
