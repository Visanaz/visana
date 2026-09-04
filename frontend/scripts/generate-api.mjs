import { existsSync } from 'node:fs';
import { resolve } from 'node:path';
import { spawnSync } from 'node:child_process';

const source = process.env.OPENAPI_SPEC_PATH;

if (source === undefined || source.length === 0) {
  console.error('FRONTEND_API_CODEGEN_BLOCKED_BY_SPEC: set OPENAPI_SPEC_PATH to an approved local OpenAPI document.');
  process.exitCode = 1;
} else {
  const specPath = resolve(source);

  if (!existsSync(specPath)) {
    console.error('FRONTEND_API_CODEGEN_BLOCKED_BY_SPEC: approved local specification was not found.');
    process.exitCode = 1;
  } else {
    const result = spawnSync(
      'openapi-generator-cli',
      ['generate', '-i', specPath, '-g', 'typescript-angular', '-o', 'src/app/core/api/generated'],
      { shell: process.platform === 'win32', stdio: 'inherit' },
    );
    process.exitCode = result.status ?? 1;
  }
}
