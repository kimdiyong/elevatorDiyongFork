import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { PRODUCTS } from '../assets/mockData';
import { useOrder } from '../context/OrderContext';

const ProductDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { setSelectedProduct } = useOrder();

    const product = PRODUCTS.find(p => p.id === parseInt(id));

    if (!product) return <div>상품을 찾을 수 없습니다.</div>;

    const handleOrder = () => {
        setSelectedProduct(product);
        navigate('/payment');
    };

    return (
        <div style={{ padding: '20px' }}>
            <h1>{product.name}</h1>

            <div style={{ border: '1px solid #ccc', padding: '20px', margin: '20px 0' }}>
                <p><strong>가격:</strong> {product.price}원</p>
                <p><strong>용량:</strong> {product.volume}</p>
                <p><strong>칼로리:</strong> {product.calories}</p>
                <p><strong>유통기한:</strong> {product.expirationDate}</p>
                <p><strong>알레르기 정보:</strong> {product.allergyInfo}</p>
                <p><strong>남은 재고:</strong> {product.stock}개</p>
            </div>

            <div style={{ display: 'flex', gap: '10px' }}>
                <button onClick={() => navigate(-1)} style={{ flex: 1, padding: '15px' }}>
                    뒤로 가기
                </button>
                <button
                    onClick={handleOrder}
                    style={{ flex: 1, padding: '15px', background: 'blue', color: 'white' }}
                >
                    결제 하기
                </button>
            </div>
        </div>
    );
};

export default ProductDetailPage;