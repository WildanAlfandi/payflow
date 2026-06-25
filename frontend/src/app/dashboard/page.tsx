'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/authStore';
import api from '@/lib/axios';
import { Account, Transaction, ApiResponse } from '@/types';
import { LogOut, Send, RefreshCw, Copy } from 'lucide-react';

export default function DashboardPage() {
  const router = useRouter();
  const { user, logout } = useAuthStore();
  const [account, setAccount] = useState<Account | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    if (!user) { router.push('/login'); return; }
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const accRes = await api.get<ApiResponse<Account>>('/api/v1/accounts/me');
      setAccount(accRes.data.data);

      const txRes = await api.get<ApiResponse<Transaction[]>>(
        `/api/v1/transactions?accountNumber=${accRes.data.data.accountNumber}`
      );
      setTransactions(txRes.data.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  const copyAccountNumber = () => {
    if (account) {
      navigator.clipboard.writeText(account.accountNumber);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  const formatCurrency = (amount: number) =>
    new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(amount);

  const formatDate = (date: string) =>
    new Date(date).toLocaleDateString('id-ID', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="text-blue-400 text-lg animate-pulse">Loading...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-950 text-white">
      {/* Header */}
      <header className="bg-gray-900 border-b border-gray-800 px-6 py-4 flex justify-between items-center">
        <h1 className="text-2xl font-bold text-blue-400">PayFlow</h1>
        <div className="flex items-center gap-4">
          <span className="text-gray-400 text-sm">Halo, {user?.fullName}</span>
          <button onClick={handleLogout} className="flex items-center gap-2 text-gray-400 hover:text-red-400 transition-colors">
            <LogOut size={18} />
            <span className="text-sm">Logout</span>
          </button>
        </div>
      </header>

      <main className="max-w-4xl mx-auto p-6 space-y-6">
        {/* Balance Card */}
        <div className="bg-gradient-to-br from-blue-600 to-blue-800 rounded-2xl p-6">
          <p className="text-blue-200 text-sm mb-1">Total Saldo</p>
          <p className="text-4xl font-bold text-white mb-4">
            {formatCurrency(account?.balance || 0)}
          </p>
          <div className="flex items-center gap-2">
            <div>
              <p className="text-blue-200 text-xs">No. Rekening</p>
              <p className="text-white font-mono font-semibold">{account?.accountNumber}</p>
            </div>
            <button onClick={copyAccountNumber} className="ml-2 text-blue-200 hover:text-white transition-colors">
              <Copy size={16} />
            </button>
            {copied && <span className="text-xs text-green-300">Tersalin!</span>}
          </div>
        </div>

        {/* Action Buttons */}
        <div className="grid grid-cols-2 gap-4">
          <button
            onClick={() => router.push('/transfer')}
            className="bg-gray-900 border border-gray-800 hover:border-blue-500 rounded-xl p-4 flex items-center gap-3 transition-colors"
          >
            <div className="bg-blue-600 rounded-lg p-2">
              <Send size={20} />
            </div>
            <div className="text-left">
              <p className="font-semibold">Transfer</p>
              <p className="text-gray-400 text-xs">Kirim uang</p>
            </div>
          </button>

          <button
            onClick={fetchData}
            className="bg-gray-900 border border-gray-800 hover:border-blue-500 rounded-xl p-4 flex items-center gap-3 transition-colors"
          >
            <div className="bg-green-600 rounded-lg p-2">
              <RefreshCw size={20} />
            </div>
            <div className="text-left">
              <p className="font-semibold">Refresh</p>
              <p className="text-gray-400 text-xs">Update saldo</p>
            </div>
          </button>
        </div>

        {/* Transaction History */}
        <div className="bg-gray-900 rounded-2xl border border-gray-800 p-6">
          <h2 className="text-lg font-semibold mb-4">Riwayat Transaksi</h2>

          {transactions.length === 0 ? (
            <p className="text-gray-500 text-center py-8">Belum ada transaksi</p>
          ) : (
            <div className="space-y-3">
              {transactions.map((tx) => {
                const isDebit = tx.sourceAccountNumber === account?.accountNumber;
                return (
                  <div key={tx.transactionId} className="flex items-center justify-between py-3 border-b border-gray-800 last:border-0">
                    <div className="flex items-center gap-3">
                      <div className={`w-10 h-10 rounded-full flex items-center justify-center text-sm font-bold ${isDebit ? 'bg-red-900/50 text-red-400' : 'bg-green-900/50 text-green-400'}`}>
                        {isDebit ? '−' : '+'}
                      </div>
                      <div>
                        <p className="font-medium text-sm">
                          {isDebit ? `Transfer ke ${tx.destinationAccountNumber}` : `Terima dari ${tx.sourceAccountNumber}`}
                        </p>
                        <p className="text-gray-500 text-xs">{formatDate(tx.transactionTime)}</p>
                        {tx.description && <p className="text-gray-400 text-xs">{tx.description}</p>}
                      </div>
                    </div>
                    <p className={`font-semibold ${isDebit ? 'text-red-400' : 'text-green-400'}`}>
                      {isDebit ? '-' : '+'}{formatCurrency(tx.amount)}
                    </p>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}