import {exec} from 'child_process';


console.log('SUCCESS!!');

exec('echo YEY!!');

const commandSetup = `
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

curl -fsSL "https://get.pnpm.io/install.sh" | env PNPM_VERSION=9.1.0 bash -

echo 1
pnpm store prune
echo 2
pnpm install -f --no-frozen-lockfile --prefer-offline false
`;

const commandPNPM = `
echo 1
pnpm store prune
echo 2
pnpm install -f --no-frozen-lockfile --prefer-offline false
`;

function execute(command: string, label: string, next?: () => Promise<any>) {
	console.log(`-------------------- STARTED ${label} -----------------------`);
	return new Promise((resolve, reject) => {
		console.log(`${label} - RUNNING!!`);
		console.log(`${label} - command: `, command);
		exec(command, {shell: '/bin/bash'}, (error, stdout, stderr) => {
			console.log(`${label} - COMPLETED RUNNING!!`);
			if (error)
				console.error(`${label} - error:`, error);
			resolve({stdout, stderr});
		});
	}).then(async ({stdout, stderr}: any) => {
		console.log(`${label} - DONE!!`);
		console.log(`${label} - stdout:`, stdout);

		console.error(`${label} - stderr:`, stderr);
		console.log(`-------------------- ENDED ${label} -----------------------`);
		return next?.();
	});
}

execute(commandSetup, 'setup');
// execute(commandSetup, 'setup', () => execute(commandPNPM, 'pnpm-install'));

