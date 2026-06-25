'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { v4 as uuidv4 } from 'uuid';
import api from '@/lib/axios';
import { ApiResponse } from '@/types';
import { ArrowLeft, Send } from 'lucide-react';

const schema = z.object({
  destinationAccountNumber: z.string().min(10, 'Nomor rekening tidak valid'),
  amount: z.number().min(1000, 'Minimal transfer Rp1.000'),
  description: z.string().optional(),
});

type FormData = z.infer<typeof schema>;

export default function TransferPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
  });

  const onSubmit = async (data: FormData) => {
    try {
      setLoading(true);
      setError('');
      setSuccess('');

      await api.post('/api/v1/accounts/transfer', {
        idempotencyKey: uuidv4(),
        destinationAccountNumber: data.destinationAccountNumber,
        amount: data.amount,
        description: data.description || '',
      });

      setSuccess('Transfer berhasil!');
      reset();
      setTimeout(() => router.push('/dashboard'), 2000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Transfer gagal');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-950 text-white">
      <header className="bg-gray-900 border-b border-gray-800 px-6 py-4 flex items-center gap-4">
        <button onClick={() => router.push('/dashboard')} className="text-gray-400 hover:text-white transition-colors">
          <ArrowLeft size={20} />
        </button>
        <h1 className="text-xl font-semibold">Transfer</h1>
      </header>

      <main className="max-w-md mx-auto p-6">
        <div className="bg-gray-900 rounded-2xl border border-gray-800 p-6">

          {error && (
            <div className="bg-red-900/30 border border-red-700 text-red-400 px-4 py-3 rounded-lg mb-4 text-sm">
              {error}
            </div>
          )}

          {success && (
            <div className="bg-green-900/30 border border-green-700 text-green-400 px-4 py-3 rounded-lg mb-4 text-sm">
              {success}
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            <div>
              <label className="text-sm text-gray-400 mb-1 block">No. Rekening Tujuan</label>
              <input
                {...register('destinationAccountNumber')}
                placeholder="8xxxxxxxxx"
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-4 py-3 text-white placeholder-gray-500 focus:outline-none focus:border-blue-500 font-mono"
              />
              {errors.destinationAccountNumber && (
                <p className="text-red-400 text-xs mt-1">{errors.destinationAccountNumber.message}</p>
              )}
            </div>

            <div>
              <label className="text-sm text-gray-400 mb-1 block">Jumlah (Rp)</label>
              <input
                {...register('amount', { valueAsNumber: true })}
                type="number"
                placeholder="100000"
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-4 py-3 text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
              />
              {errors.amount && <p className="text-red-400 text-xs mt-1">{errors.amount.message}</p>}
            </div>

            <div>
              <label className="text-sm text-gray-400 mb-1 block">Keterangan (opsional)</label>
              <input
                {...register('description')}
                placeholder="Bayar makan siang"
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-4 py-3 text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-blue-800 text-white font-semibold py-3 rounded-lg flex items-center justify-center gap-2 transition-colors"
            >
              <Send size={18} />
              {loading ? 'Memproses...' : 'Transfer Sekarang'}
            </button>
          </form>
        </div>
      </main>
    </div>
  );
}