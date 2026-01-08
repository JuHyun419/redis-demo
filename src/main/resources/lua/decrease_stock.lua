-- 재고 차감 Lua Script (원자적 연산)
-- KEYS[1]: 상품 키 (예: product:PRODUCT-001)
-- ARGV[1]: 차감 수량

local key = KEYS[1]
local quantity = tonumber(ARGV[1])

-- 현재 상품 데이터 조회
local productJson = redis.call('GET', key)

if not productJson then
    return cjson.encode({success = false, error = "PRODUCT_NOT_FOUND", message = "상품을 찾을 수 없습니다"})
end

-- JSON 파싱
local product = cjson.decode(productJson)
local currentStock = tonumber(product.stock)

-- 재고 확인
if currentStock < quantity then
    return cjson.encode({
        success = false, 
        error = "OUT_OF_STOCK", 
        message = "재고 부족",
        currentStock = currentStock,
        requestedQuantity = quantity
    })
end

-- 재고 차감
local previousStock = currentStock
product.stock = currentStock - quantity

-- 저장
redis.call('SET', key, cjson.encode(product))

-- 결과 반환
return cjson.encode({
    success = true,
    productId = product.productId,
    previousStock = previousStock,
    decreasedAmount = quantity,
    currentStock = product.stock
})
