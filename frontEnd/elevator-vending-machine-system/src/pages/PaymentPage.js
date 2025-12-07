import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useOrder } from '../context/OrderContext';

const PaymentPage = () => {
    const { selectedProduct } = useOrder();
    const navigate = useNavigate();
    const [paymentStep, setPaymentStep] = useState('SELECT'); // SELECT, PROCESS, DONE
    const [message, setMessage] = useState('');

    if (!selectedProduct) {
        navigate('/list');
        return null;
    }

    const processPayment = (method) => {
        setPaymentStep('PROCESS');
        setMessage(method === 'CARD' ? '카드를 투입구에 넣어주세요...' : '현금을 투입해주세요...');

        // 결제 처리 시뮬레이션 (3초 후 완료)
        setTimeout(() => {
            navigate('/result'); // 결과 페이지로 이동
        }, 3000);
    };

    return (
        <div style={{ padding: '20px', textAlign: 'center' }}>
            <h2>결제 화면</h2>
            <h3>총 결제 금액: {selectedProduct.price}원</h3>

            {paymentStep === 'SELECT' && (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '20px', marginTop: '50px' }}>
                    <button onClick={() => processPayment('CASH')} style={{ padding: '30px', fontSize: '1.2rem' }}>
                        현금 결제
                    </button>
                    <button onClick={() => processPayment('CARD')} style={{ padding: '30px', fontSize: '1.2rem' }}>
                        카드 결제
                    </button>
                </div>
            )}

            {paymentStep === 'PROCESS' && (
                <div style={{ marginTop: '50px' }}>
                    <p>{message}</p>
                    <p>처리중입니다...</p>
                </div>
            )}
        </div>
    );
};

export default PaymentPage;