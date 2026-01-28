package com.momento.event.dto;

import com.momento.event.model.TypeVO;
import lombok.Data;

@Data
public class TypeMenuDTO {
    private Integer typeId;
    private String typeName;
    private String lucideIcon;
    private String colorClass;

    public TypeMenuDTO(TypeVO type) {
        this.typeId = type.getTypeId();
        this.typeName = type.getTypeName();
        this.lucideIcon = determineLucideIcon(type.getTypeName());
        this.colorClass = determineColor(type.getTypeName());
    }

    private String determineLucideIcon(String name) {
        if (name == null)
            return "tag";

        if (name.contains("音樂") || name.contains("演唱"))
            return "music";
        if (name.contains("展覽") || name.contains("藝術"))
            return "palette";
        if (name.contains("表演") || name.contains("戲劇"))
            return "masks-theater";
        if (name.contains("講座") || name.contains("研討"))
            return "mic";
        if (name.contains("工坊") || name.contains("手作"))
            return "hand";
        if (name.contains("影像") || name.contains("多媒體") || name.contains("電影"))
            return "video";
        if (name.contains("文化") || name.contains("節慶") || name.contains("民俗"))
            return "landmark";
        if (name.contains("運動") || name.contains("休閒") || name.contains("賽事"))
            return "trophy";
        if (name.contains("生活") || name.contains("風格"))
            return "leaf";
        if (name.contains("市集") || name.contains("購物"))
            return "store";
        if (name.contains("旅遊") || name.contains("觀光"))
            return "map";
        if (name.contains("設計") || name.contains("創意"))
            return "lightbulb";

        return "tag";
    }

    private String determineColor(String name) {
        if (name == null)
            return "text-secondary";

        if (name.contains("音樂") || name.contains("演唱"))
            return "text-success";
        if (name.contains("展覽") || name.contains("藝術"))
            return "text-info";
        if (name.contains("表演") || name.contains("戲劇"))
            return "text-warning";
        if (name.contains("講座") || name.contains("研討"))
            return "text-info";
        if (name.contains("工坊") || name.contains("手作"))
            return "text-danger";
        if (name.contains("影像") || name.contains("多媒體") || name.contains("電影"))
            return "text-secondary";
        if (name.contains("文化") || name.contains("節慶") || name.contains("民俗"))
            return "text-danger";
        if (name.contains("運動") || name.contains("休閒") || name.contains("賽事"))
            return "text-primary";
        if (name.contains("生活") || name.contains("風格"))
            return "text-success";
        if (name.contains("市集") || name.contains("購物"))
            return "text-warning";
        if (name.contains("旅遊") || name.contains("觀光"))
            return "text-success";
        if (name.contains("設計") || name.contains("創意"))
            return "text-warning";

        return "text-secondary";
    }
}
