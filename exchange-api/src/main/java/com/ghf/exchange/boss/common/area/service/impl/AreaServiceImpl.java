package com.ghf.exchange.boss.common.area.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.common.area.dto.*;
import com.ghf.exchange.boss.common.area.entity.Area;
import com.ghf.exchange.boss.common.area.entity.QArea;
import com.ghf.exchange.boss.common.area.enums.AreaStatusEnum;
import com.ghf.exchange.boss.common.area.event.UpdateFullAreaEvent;
import com.ghf.exchange.boss.common.area.repository.AreaRepository;
import com.ghf.exchange.boss.common.area.service.AreaService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.BaseIdDTO;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class AreaServiceImpl extends BaseServiceImpl<Area, Long> implements AreaService {

    @Lazy
    @Resource
    private AreaService areaService;

    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;
    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public AreaServiceImpl(AreaRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Area", key = "'pageArea:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.areaType) ", condition = " T(org.springframework.util.StringUtils).isEmpty(#p0.areaname) &&  T(org.springframework.util.StringUtils).isEmpty(#p0.areadesc)  && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<AreaRespDTO>> pageArea(PageAreaReqDTO pageAreaReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageAreaReqDTO.getAreaname())) {
            predicate.and(QArea.area.areaname.contains(pageAreaReqDTO.getAreaname()));
        }
        if (!ObjectUtils.isEmpty(pageAreaReqDTO.getAreadesc())) {
            predicate.and(QArea.area.areadesc.contains(pageAreaReqDTO.getAreadesc()));
        }
        if (pageAreaReqDTO.getAreaType() != 0) {
            predicate.and(QArea.area.areaType.eq(pageAreaReqDTO.getAreaType()));
        }
        PageRespDTO<AreaRespDTO> pageRespDTO = areaService.page(predicate, pageAreaReqDTO, AreaRespDTO.class);

        pageRespDTO.getList().forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getFullAreaname())) {
                e.setFullAreanameList(Arrays.stream(e.getFullAreaname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
            if (!ObjectUtils.isEmpty(e.getFullAreadesc())) {
                e.setFullAreadescList(Arrays.stream(e.getFullAreadesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
        });
        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Area", key = "'treeArea:'.concat(#p0.areaname).concat(#p0.treeDeep)")
    @Override
    @SneakyThrows
    public Result<AreaRespDTO> treeArea(TreeAreaReqDTO treeAreaReqDTO) {
        //?????????
        AreaRespDTO areaRespDTO = null;
        if (ObjectUtils.isEmpty(treeAreaReqDTO.getAreaname())) {
            Predicate predicate = QArea.area.parentAreaname.isEmpty().or(QArea.area.parentAreaname.isNull());
            //????????????????????????????????????????????????
            areaRespDTO = this.list(predicate, AreaRespDTO.class).get(0);

        } else {
            GetAreaByAreanameReqDTO getAreaByAreanameReqDTO = new GetAreaByAreanameReqDTO();
            getAreaByAreanameReqDTO.setAreaname(treeAreaReqDTO.getAreaname());
            areaRespDTO = areaService.getAreaByAreaname(getAreaByAreanameReqDTO).getData();
        }

        BooleanBuilder predicate = new BooleanBuilder();
        //????????????????????????????????????
        predicate.and(QArea.area.status.eq(AreaStatusEnum.ENABLE.getCode()));
        if (treeAreaReqDTO.getTreeDeep() != 0) {
            predicate.and(QArea.area.deep.goe(areaRespDTO.getDeep())
                    .and(QArea.area.deep.lt(areaRespDTO.getDeep() + treeAreaReqDTO.getTreeDeep())));
        }
        predicate.and(QArea.area.fullAreaname.contains("," + areaRespDTO.getAreaname() + ","));

        List<AreaRespDTO> originList = areaService.list(predicate, AreaRespDTO.class);

        originList.forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getFullAreaname())) {
                e.setFullAreanameList(Arrays.stream(e.getFullAreaname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
            if (!ObjectUtils.isEmpty(e.getFullAreadesc())) {
                e.setFullAreadescList(Arrays.stream(e.getFullAreadesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
        });

        //???????????????????????????
        List<AreaRespDTO> child = getAreaChildren(originList, areaRespDTO.getAreaname());
        areaRespDTO.setChildren(child);

        return new Result<>(areaRespDTO);
    }

    private List<AreaRespDTO> getAreaChildren(List<AreaRespDTO> originList, String parentAreaname) {
        //???????????????
        List<AreaRespDTO> childList = new ArrayList<>();
        Iterator<AreaRespDTO> it = originList.iterator();
        while (it.hasNext()) {
            AreaRespDTO next = it.next();
            //????????????????????????????????????????????????????????????
            if (parentAreaname.equals(next.getParentAreaname())) {
                childList.add(next);
                it.remove();
            }
        }

        //?????? ?????????????????????????????????
        for (AreaRespDTO areaRespDTO : childList) {
            List<AreaRespDTO> child = getAreaChildren(originList, areaRespDTO.getAreaname());
            areaRespDTO.setChildren(child);
        }
        return childList;
    }

    @Cacheable(cacheNames = "Area", key = "'ListAncestorByAreaname:'+#p0.areaname")
    @Override
    @SneakyThrows
    public Result<List<AreaRespDTO>> listAncestorByAreaname(ListAncestorByAreanameReqDTO listAncestorByAreanameReqDTO) {

        BooleanBuilder predicate = new BooleanBuilder();
        //????????????????????????????????????
        predicate.and(QArea.area.status.eq(AreaStatusEnum.ENABLE.getCode()));
        List<AreaRespDTO> originList = areaService.list(predicate, AreaRespDTO.class);

        originList.forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getFullAreaname())) {
                e.setFullAreanameList(Arrays.stream(e.getFullAreaname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
            if (!ObjectUtils.isEmpty(e.getFullAreadesc())) {
                e.setFullAreadescList(Arrays.stream(e.getFullAreadesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
        });

        List<AreaRespDTO> nodes = new ArrayList<>();

        //????????????
        AreaRespDTO currentAreaRespDTO = null;
        Iterator<AreaRespDTO> it0 = originList.iterator();
        while (it0.hasNext()) {
            AreaRespDTO areaRespDTO = it0.next();
            if (areaRespDTO.getAreaname().equals(listAncestorByAreanameReqDTO.getAreaname())) {

                nodes.add(0, areaRespDTO);
                currentAreaRespDTO = areaRespDTO;
                it0.remove();

                //?????????????????????,??????????????????????????????????????????
                break;
            }
        }

        while (currentAreaRespDTO != null && !ObjectUtils.isEmpty(currentAreaRespDTO.getParentAreaname())) {
            //????????????????????????????????????
            boolean flag = false;
            Iterator<AreaRespDTO> it = originList.iterator();
            while (it.hasNext()) {
                AreaRespDTO next = it.next();
                if (currentAreaRespDTO.getParentAreaname().equals(next.getAreaname())) {

                    nodes.add(0, next);
                    currentAreaRespDTO = next;
                    it.remove();
                    flag = true;

                    //?????????????????????,??????????????????????????????????????????
                    break;
                }
            }
            //??????????????????????????????
            if (!flag) {
                break;
            }
        }
        return new Result<>(nodes);
    }

    @Cacheable(cacheNames = "Area", key = "'getAreaByAreaname:'+#p0.areaname")
    @Override
    @SneakyThrows
    public Result<AreaRespDTO> getAreaByAreaname(GetAreaByAreanameReqDTO getAreaByAreanameReqDTO) {
        //TODO ????????????
        String areaname = getAreaByAreanameReqDTO.getAreaname();
        Predicate predicate = QArea.area.areaname.eq(areaname);
        Area area = areaService.get(predicate);
        //??????
        AreaRespDTO areaRespDTO = ModelMapperUtil.map(area, AreaRespDTO.class);

        if (!ObjectUtils.isEmpty(areaRespDTO.getFullAreaname())) {
            areaRespDTO.setFullAreanameList(Arrays.stream(areaRespDTO.getFullAreaname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }
        if (!ObjectUtils.isEmpty(areaRespDTO.getFullAreadesc())) {
            areaRespDTO.setFullAreadescList(Arrays.stream(areaRespDTO.getFullAreadesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }

        return new Result<>(areaRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<AreaRespDTO> getAreaByAreaId(BaseIdDTO baseIdDto) {
        //TODO ????????????
        long id = baseIdDto.getId();
        Predicate predicate = QArea.area.id.eq(id);
        Area area = areaService.get(predicate);
        //??????
        AreaRespDTO areaRespDTO = ModelMapperUtil.map(area, AreaRespDTO.class);

        if (!ObjectUtils.isEmpty(areaRespDTO.getFullAreaname())) {
            areaRespDTO.setFullAreanameList(Arrays.stream(areaRespDTO.getFullAreaname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }
        if (!ObjectUtils.isEmpty(areaRespDTO.getFullAreadesc())) {
            areaRespDTO.setFullAreadescList(Arrays.stream(areaRespDTO.getFullAreadesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }

        return new Result<>(areaRespDTO);
    }

    @Override
    public Result<AreaRespDTO> getAreaByAreaDesc(GetAreaByAreadescReqDTO getAreaByAreadescReqDTO) {
        //TODO ????????????
        String areadesc = getAreaByAreadescReqDTO.getAreadesc();
        Predicate predicate = QArea.area.areadesc.eq(areadesc);
        Area area = areaService.get(predicate);
        AreaRespDTO areaRespDTO = ModelMapperUtil.map(area, AreaRespDTO.class);

        if (!ObjectUtils.isEmpty(areaRespDTO.getFullAreaname())) {
            areaRespDTO.setFullAreanameList(Arrays.stream(areaRespDTO.getFullAreaname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }
        if (!ObjectUtils.isEmpty(areaRespDTO.getFullAreadesc())) {
            areaRespDTO.setFullAreadescList(Arrays.stream(areaRespDTO.getFullAreadesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }

        return new Result<>(areaRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsAreaByAreaname(GetAreaByAreanameReqDTO getAreaByAreanameReqDTO) {
        //TODO ????????????
        String areaname = getAreaByAreanameReqDTO.getAreaname();
        Predicate predicate = QArea.area.areaname.eq(areaname);
        boolean b = areaService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "Area", allEntries = true)

    @Override
    @SneakyThrows
    public Result<Void> addArea(AddAreaReqDTO addAreaReqDTO) {
        Area area = ModelMapperUtil.map(addAreaReqDTO, Area.class);
        //??????????????????????????????

        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        area.setCreateUserId(currentLoginUser.getId());
        area.setCreateUserName(currentLoginUser.getUsername());

        area.setCreateTime(new Date());
        //???????????????
        String areaname = area.getAreaname();
        GetAreaByAreanameReqDTO getAreaByAreanameReqDTO = new GetAreaByAreanameReqDTO();
        getAreaByAreanameReqDTO.setAreaname(areaname);
        boolean b = areaService.existsAreaByAreaname(getAreaByAreanameReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.AREA_EXISTS);
        }
        //?????????id
        area.setId(IdUtil.generateLongId());
        area.setStatus(AreaStatusEnum.ENABLE.getCode());

        //??????????????????
        if (!ObjectUtils.isEmpty(area.getParentAreaname())) {
            GetAreaByAreanameReqDTO getParentAreaByAreanameReqDTO = new GetAreaByAreanameReqDTO();
            getParentAreaByAreanameReqDTO.setAreaname(area.getParentAreaname());
            AreaRespDTO parentAreaRespDTO = areaService.getAreaByAreaname(getParentAreaByAreanameReqDTO).getData();
            Area parentArea = ModelMapperUtil.map(parentAreaRespDTO, Area.class);
            area.setParentAreaId(parentArea.getId());
            area.setParentAreaname(parentArea.getAreaname());
            area.setParentAreadesc(parentArea.getAreadesc());
        } else {
            area.setParentAreaId(0);
            area.setParentAreaname(null);
            area.setParentAreadesc(null);
        }

        //??????????????????
        areaService.add(area);

        //?????????????????????????????????????????????
        applicationEventPublisher.publishEvent(new UpdateFullAreaEvent(getAreaByAreanameReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Area", allEntries = true)

    @Override
    @SneakyThrows
    public Result<Void> updateAreaByAreaname(UpdateAreaByAreanameReqDTO updateAreaByAreanameReqDTO) {

        String areaname = updateAreaByAreanameReqDTO.getAreaname();
        String areadesc = updateAreaByAreanameReqDTO.getAreadesc();
        Integer areaType = updateAreaByAreanameReqDTO.getAreaType();
        int orderNum = updateAreaByAreanameReqDTO.getOrderNum();
        String parentAreaname = updateAreaByAreanameReqDTO.getParentAreaname();
        //??????
        GetAreaByAreanameReqDTO getAreaByAreanameReqDTO = new GetAreaByAreanameReqDTO();
        getAreaByAreanameReqDTO.setAreaname(areaname);
        AreaRespDTO afterAreaRespDTO = areaService.getAreaByAreaname(getAreaByAreanameReqDTO).getData();
        Area afterArea = ModelMapperUtil.map(afterAreaRespDTO, Area.class);
        if (afterArea == null) {
            return new Result<>(ResultCodeEnum.AREA_NOT_EXISTS);
        }
        String oldParentareaname = afterArea.getParentAreaname();
        String oldAreadesc = afterArea.getAreadesc();

        //?????????
        afterArea.setAreadesc(areadesc);
        afterArea.setAreaType(areaType);
        afterArea.setOrderNum(orderNum);
        //??????????????????
        if (!ObjectUtils.isEmpty(parentAreaname)) {
            GetAreaByAreanameReqDTO getParentAreaByAreanameReqDTO = new GetAreaByAreanameReqDTO();
            getParentAreaByAreanameReqDTO.setAreaname(parentAreaname);
            AreaRespDTO parentAreaRespDTO = areaService.getAreaByAreaname(getParentAreaByAreanameReqDTO).getData();
            Area parentArea = ModelMapperUtil.map(parentAreaRespDTO, Area.class);

            afterArea.setParentAreaId(parentArea.getId());
            afterArea.setParentAreaname(parentArea.getAreaname());
            afterArea.setParentAreadesc(parentArea.getAreadesc());
        } else {
            afterArea.setParentAreaId(0);
            afterArea.setParentAreaname(null);
            afterArea.setParentAreadesc(null);
        }

        //??????????????????
        areaService.update(afterArea);

        //?????????????????????????????????????????????
        applicationEventPublisher.publishEvent(new UpdateFullAreaEvent(getAreaByAreanameReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Area", allEntries = true)

    @Override
    @SneakyThrows
    public Result<Void> enableArea(GetAreaByAreanameReqDTO getAreaByAreanameReqDTO) {
        String areaname = getAreaByAreanameReqDTO.getAreaname();
        //??????
        AreaRespDTO afterAreaRespDTO = areaService.getAreaByAreaname(getAreaByAreanameReqDTO).getData();
        Area afterArea = ModelMapperUtil.map(afterAreaRespDTO, Area.class);
        if (afterArea == null) {
            return new Result<>(ResultCodeEnum.AREA_NOT_EXISTS);
        }
        //?????????
        afterArea.setStatus(AreaStatusEnum.ENABLE.getCode());
        //??????????????????
        areaService.update(afterArea);

        //?????????????????????????????????????????????
        applicationEventPublisher.publishEvent(new UpdateFullAreaEvent(getAreaByAreanameReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Area", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disableArea(GetAreaByAreanameReqDTO getAreaByAreanameReqDTO) {
        String areaname = getAreaByAreanameReqDTO.getAreaname();
        //??????
        AreaRespDTO afterAreaRespDTO = areaService.getAreaByAreaname(getAreaByAreanameReqDTO).getData();
        Area afterArea = ModelMapperUtil.map(afterAreaRespDTO, Area.class);
        if (afterArea == null) {
            return new Result<>(ResultCodeEnum.AREA_NOT_EXISTS);
        }
        //?????????
        afterArea.setStatus(AreaStatusEnum.DISABLE.getCode());
        //??????????????????
        areaService.update(afterArea);
        return new Result<>(ResultCodeEnum.OK);
    }

}