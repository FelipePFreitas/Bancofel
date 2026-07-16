import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080';

  client(type: 'pf' | 'pj', document: string, payload?: unknown, action: 'create' | 'update' | 'deactivate' | 'reactivate' | 'find' = 'create') {
    const url = `${this.baseUrl}/clientes/${type}${action === 'create' ? '' : '/' + encodeURIComponent(document)}${action === 'reactivate' ? '/reativar' : ''}`;
    if (action === 'find') return this.http.get(url);
    if (action === 'update') return this.http.put(url, payload);
    if (action === 'deactivate') return this.http.delete(url);
    if (action === 'reactivate') return this.http.patch(url, {});
    return this.http.post(url, payload);
  }
  saldo(account: string) { return this.http.get(`${this.baseUrl}/conta/${encodeURIComponent(account)}`, { responseType: 'text' }); }
  pixKey(account: string, key: string) { return this.http.post(`${this.baseUrl}/conta/${encodeURIComponent(account)}/chaves-pix/${encodeURIComponent(key)}`, {}); }
  transaction(type: string, account: string, value: number, destination?: string) {
    const endpoint = type === 'transferencia' ? `transferencia/${account}/para/${destination}` : type === 'pix' ? `pix/${account}/para/${destination}` : `${type}/${account}`;
    return this.http.put(`${this.baseUrl}/transacao/${endpoint}?valor=${value}`, {});
  }
}
