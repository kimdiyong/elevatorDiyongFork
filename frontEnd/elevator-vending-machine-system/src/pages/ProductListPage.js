import React from 'react';
import { useNavigate } from 'react-router-dom';
import { PRODUCTS } from '../assets/mockData';
import styled from 'styled-components';

const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding: 20px;
`;

const Card = styled.div`
  border: 1px solid #ddd;
  padding: 15px;
  text-align: center;
  opacity: ${props => (props.soldOut ? 0.5 : 1)};
  pointer-events: ${props => (props.soldOut ? 'none' : 'auto')};
`;

const ProductListPage = () => {
    const navigate = useNavigate();

    return (
        <div>
            <h2>상품을 선택하세요</h2>
            <Grid>
                {PRODUCTS.map((product) => (
                    <Card
                        key={product.id}
                        soldOut={product.stock <= 0}
                        onClick={() => navigate(`/detail/${product.id}`)}
                    >
                        {/* 실제 이미지가 없으면 박스로 대체 */}
                        <div style={{height: '100px', background: '#eee', marginBottom: '10px'}}>
                            {product.name}
                        </div>
                        <h3>{product.name}</h3>
                        <p>{product.price}원</p>
                        {product.stock <= 0 && <span style={{color: 'red'}}>품절</span>}
                    </Card>
                ))}
            </Grid>
        </div>
    );
};

export default ProductListPage;