import { spawnSync } from 'node:child_process';

const unsupported = process.argv.slice(2).filter((argument) => argument !== '--run');

if (unsupported.length > 0) {
  console.error(`Unsupported test arguments: ${unsupported.join(', ')}`);
  process.exitCode = 1;
} else {
  const result = spawnSync(process.execPath, ['node_modules/@angular/cli/bin/ng.js', 'test', '--watch=false'], {
    stdio: 'inherit',
  });
  process.exitCode = result.status ?? 1;
}
