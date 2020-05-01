package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import com.upgrad.FoodOrderingApp.service.common.ItemType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "item")
@NamedNativeQueries({
  // Using native query as named queries do not support LIMIT in nested statements.
  @NamedNativeQuery(
      name = "topFivePopularItemsByRestaurant",
      query =
          "select * from item where id in "
              + "(select item_id from order_item where order_id in "
              + "(select id from orders where restaurant_id = ? ) "
              + "group by order_item.item_id "
              + "order by (count(order_item.order_id)) "
              + "desc LIMIT 5)",
      resultClass = ItemEntity.class),
  @NamedNativeQuery(
                name = "getAllItemsInCategoryInRestaurant",
                query = "select * from item where id in (select item_id " +
                        "from restaurant_item " +
                        "inner join restaurant_category on restaurant_item.restaurant_id=restaurant_category.restaurant_id " +
                        "where restaurant_item.restaurant_id = ? and restaurant_category.category_id = ?)",
          resultClass = ItemEntity.class),
})



public class ItemEntity implements Serializable {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @Size(max = 200)
  @Column(name = "uuid", unique = true)
  private String uuid;

  @NotNull
  @Size(max = 30)
  @Column(name = "item_name")
  private String itemName;

  @NotNull
  @Column(name = "price")
  private Integer price;

  @NotNull
  @Size(max = 10)
  @Column(name = "type")
  private String type;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinTable(
          name = "restaurant_item",
          joinColumns = @JoinColumn(name = "item_id"),
          inverseJoinColumns = @JoinColumn(name = "restaurant_id"))
  private RestaurantEntity restaurantEntity;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinTable(
          name = "category_item",
          joinColumns = @JoinColumn(name = "item_id"),
          inverseJoinColumns = @JoinColumn(name = "category_id"))
  private CategoryEntity categoryEntity;

  public ItemEntity() {}

  public ItemEntity(
      @NotNull @Size(max = 200) String uuid,
      @NotNull @Size(max = 30) String itemName,
      @NotNull Integer price,
      @NotNull @Size(max = 10) String type) {
    this.uuid = uuid;
    this.itemName = itemName;
    this.price = price;
    this.type = type;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public String getType() {
    return type;
  }

  public void setType(ItemType type) {
    this.type = type.toString();
  }

  public void setType(String type) {
    this.type = type;
  }

  public RestaurantEntity getRestaurantEntity() {
    return restaurantEntity;
  }

  public void setRestaurantEntity(RestaurantEntity restaurantEntity) {
    this.restaurantEntity = restaurantEntity;
  }

  public CategoryEntity getCategoryEntity() {
    return categoryEntity;
  }

  public void setCategoryEntity(CategoryEntity categoryEntity) {
    this.categoryEntity = categoryEntity;
  }

  @Override
  public boolean equals(Object obj) {
    return new EqualsBuilder().append(this, obj).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(this).hashCode();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
