import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from './api.service';

type View = 'inicio' | 'clientes' | 'contas' | 'movimentar';
@Component({ selector: 'app-root', standalone: true, imports: [CommonModule, ReactiveFormsModule], templateUrl: './app.component.html', styleUrl: './app.component.css' })
export class AppComponent {
  private fb = inject(FormBuilder); private api = inject(ApiService);
  view: View = 'inicio'; menuOpen = false; loading = false;
  notice = ''; error = ''; result = ''; clientAction: 'create' | 'find' | 'update' | 'deactivate' | 'reactivate' = 'create';
  clientForm = this.fb.group({ type: ['pf' as 'pf' | 'pj'], document: [''], nome: ['', Validators.required], dataNascimento: [''], inscricaoEstadual: [''], logradouro: ['', Validators.required], endereco: ['', Validators.required], numero: ['', Validators.required], bairro: ['', Validators.required], cep: ['', Validators.required], cidade: ['', Validators.required], estado: ['', Validators.required], status: [true] });
  accountForm = this.fb.group({ numeroConta: ['', Validators.required], chavePix: [''] });
  transactionForm = this.fb.group({ tipo: ['deposito'], numeroConta: ['', Validators.required], destino: [''], valor: [null as number | null, [Validators.required, Validators.min(0.01)]] });
  nav(view: View) { this.view = view; this.menuOpen = false; this.clear(); }
  clear() { this.notice = ''; this.error = ''; this.result = ''; }
  setAction(action: typeof this.clientAction) { this.clientAction = action; this.clear(); }
  private request<T>(source: { subscribe: (handlers: { next: (x: T) => void; error: (e: any) => void }) => void }, success: (data: T) => void) { this.loading = true; this.clear(); source.subscribe({ next: data => { this.loading = false; success(data); }, error: e => { this.loading = false; this.error = e?.error?.message || e?.error?.erro || 'Não foi possível concluir a operação. Verifique se a API está em execução e os dados informados.'; } }); }
  sendClient() {
    const f = this.clientForm.getRawValue(); const type = f.type; const document = f.document?.replace(/\D/g, '') || '';
    if (!document) { this.error = type === 'pf' ? 'Informe o CPF.' : 'Informe o CNPJ.'; return; }
    if (this.clientAction === 'find' || this.clientAction === 'deactivate' || this.clientAction === 'reactivate') {
      this.request(this.api.client(type!, document, undefined, this.clientAction), data => { this.result = JSON.stringify(data, null, 2); this.notice = this.clientAction === 'find' ? 'Cliente localizado.' : 'Status do cliente atualizado.'; }); return;
    }
    if (this.clientForm.invalid) { this.clientForm.markAllAsTouched(); return; }
const payload: any = { nome: f.nome, logradouro: f.logradouro, endereco: f.endereco, numero: f.numero, bairro: f.bairro, cep: f.cep?.replace(/\D/g, ''), cidade: f.cidade, estado: f.estado, status: f.status, clienteTipo: type === 'pf' ? 'PESSOA_FISICA' : 'PESSOA_JURIDICA' };
    if (type === 'pf') { payload.cpf = document; payload.dataNascimento = f.dataNascimento; } else { payload.cnpj = document; payload.inscricaoEstadual = f.inscricaoEstadual; }
    this.request(this.api.client(type!, document, payload, this.clientAction), data => { this.result = JSON.stringify(data, null, 2); this.notice = this.clientAction === 'create' ? 'Cliente cadastrado com sucesso.' : 'Dados atualizados com sucesso.'; });
  }
  checkBalance() { const account = this.accountForm.value.numeroConta; if (!account) return; this.request(this.api.saldo(account), data => { this.result = `Saldo disponível: R$ ${Number(data).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`; this.notice = 'Consulta realizada.'; }); }
  addPix() { const { numeroConta, chavePix } = this.accountForm.value; if (!numeroConta || !chavePix) { this.error = 'Informe a conta e a chave Pix.'; return; } this.request(this.api.pixKey(numeroConta, chavePix), () => this.notice = 'Chave Pix cadastrada com sucesso.'); }
sendTransaction() {
    const f = this.transactionForm.getRawValue(); // Mudado de .value para .getRawValue()
    if (this.transactionForm.invalid || !f.numeroConta || f.valor === null) {
      this.transactionForm.markAllAsTouched();
      return;
    }
    if ((f.tipo === 'transferencia' || f.tipo === 'pix') && !f.destino) {
      this.error = 'Informe a conta de destino ou a chave Pix.';
      return;
    }
    // Adicionado f.valor! abaixo
    this.request(this.api.transaction(f.tipo!, f.numeroConta!, f.valor!, f.destino || undefined), data => {
      this.result = typeof data === 'object' ? JSON.stringify(data, null, 2) : `Novo saldo: R$ ${Number(data).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`;
      this.notice = 'Operação realizada com sucesso.';
    });
  }}
