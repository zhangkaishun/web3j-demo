package org.zks.match.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DepthItemVo implements Comparable<DepthItemVo> {

    /**
     * 价格
     */
    private BigDecimal price = BigDecimal.ZERO;;

    /**
     * 数量
     */
    private BigDecimal volume = BigDecimal.ZERO;


    @Override
    public int compareTo(DepthItemVo o) {
        return this.price.compareTo(o.getPrice());
    }
}
