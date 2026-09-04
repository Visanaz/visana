import { Component, input } from '@angular/core';

@Component({
  selector: 'app-status-page',
  template: `
    <section class="bg-surface mx-auto max-w-xl rounded-lg border border-border p-8">
      <h1 class="text-primary text-2xl font-semibold">{{ heading() }}</h1>
      <p class="text-muted mt-3">{{ description() }}</p>
    </section>
  `,
})
export class StatusPage {
  readonly description = input.required<string>();
  readonly heading = input.required<string>();
}
