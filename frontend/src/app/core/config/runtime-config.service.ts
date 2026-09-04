import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { RuntimeConfig, RuntimeConfigStatus, runtimeConfigSchema } from './runtime-config.model';

@Injectable({ providedIn: 'root' })
export class RuntimeConfigService {
  private readonly http = inject(HttpClient);
  private readonly value = signal<RuntimeConfig | null>(null);

  readonly config = this.value.asReadonly();
  readonly status = signal<RuntimeConfigStatus>('loading');
  readonly isOidcConfigured = computed(() => this.value()?.oidc !== null && this.status() === 'ready');

  async load(): Promise<void> {
    try {
      const candidate = await firstValueFrom(this.http.get<unknown>('assets/runtime-config.json'));
      const parsed = runtimeConfigSchema.safeParse(candidate);

      if (!parsed.success) {
        this.status.set('invalid');
        return;
      }

      this.value.set(parsed.data);
      this.status.set('ready');
    } catch {
      this.status.set('invalid');
    }
  }
}
