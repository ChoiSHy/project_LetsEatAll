package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.*;
import com.letseatall.letseatall.data.Entity.Review.ImageFile;
import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.Entity.menu.MenuImageFile;
import com.letseatall.letseatall.data.dto.Menu.MenuListDto;
import com.letseatall.letseatall.data.dto.Menu.MenuModifyDto;
import com.letseatall.letseatall.data.dto.common.IntChangeDto;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.data.repository.*;
import com.letseatall.letseatall.data.repository.Menu.MenuImageFileRepository;
import com.letseatall.letseatall.data.repository.Menu.MenuRepository;
import com.letseatall.letseatall.data.repository.review.ReviewRepository;
import com.letseatall.letseatall.service.MenuService;
import com.letseatall.letseatall.service.awsS3.S3UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl implements MenuService {
    final RestaurantRepository restaurantRepository;
    final MenuRepository menuRepository;
    final YoutubeRepository youtubeRepository;
    final UserRepository userRepository;
    final CategoryRepository categoryRepository;
    final FranchiseRepository franchiseRepository;
    final ReviewRepository reviewRepository;
    final S3UploadService s3UploadService;
    final MenuImageFileRepository imgRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(MenuServiceImpl.class);

    @Autowired
    public MenuServiceImpl(RestaurantRepository restaurantRepository,
                           MenuRepository menuRepository,
                           YoutubeRepository youtubeRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           FranchiseRepository franchiseRepository,
                           ReviewRepository reviewRepository,
                           S3UploadService s3UploadService,
                           MenuImageFileRepository imgRepository
    ) {
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.youtubeRepository = youtubeRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.franchiseRepository = franchiseRepository;
        this.reviewRepository = reviewRepository;
        this.s3UploadService = s3UploadService;
        this.imgRepository = imgRepository;
    }

    @Override
    /* 메뉴 정보 저장 */
    public MenuResponseDto saveMenu(MenuDto menuDto, MultipartFile file) throws IOException {
        LOGGER.info("[saveMenu] : saveMenu 시작 menu = {}", menuDto);
        Restaurant foundRest = restaurantRepository.findById(menuDto.getRid()).get();
        LOGGER.info("[saveMenu] : 검색된 음식점 = {}", foundRest.getName());
        Category category = categoryRepository.findById(menuDto.getCategory()).get();
        LOGGER.info("[saveMenu] : 카테고리 = {}", category.getName());
        LOGGER.info("[saveMenu] : 데이터 주입 시작");
        Menu menu = new Menu();
        menu.setName(menuDto.getName());
        menu.setPrice(menuDto.getPrice());
        menu.setScore(0);
        menu.setRestaurant(foundRest);
        menu.setCategory(category);
        menu.setInfo(menuDto.getInfo());

        LOGGER.info("[saveMenu] : 이미지 저장 시작");
        MenuImageFile mimg = null;
        if (!file.isEmpty()) {
            String[] res = s3UploadService.uploadFileToS3(file, "Images/Menu");
            mimg = new MenuImageFile();
            mimg.setMenu(menu);
            mimg.setUrl(res[0]);
            mimg.setStoredName(res[1]);
        }
        menu.setImg(mimg);
        Menu savedMenu = menuRepository.save(menu);
        LOGGER.info("[saveMenu] : 데이터 DB 저장 성공 -> savedMenu = {}", savedMenu.getName());

        MenuResponseDto menuResponseDto = makeDto(savedMenu);
        return menuResponseDto;
    }

    @Override
    public MenuResponseDto saveFranchiseMenu(MenuDto menuDto, MultipartFile file) throws IOException {
        Long fid = menuDto.getRid();
        String name = menuDto.getName();
        int price = menuDto.getPrice();
        int cid = menuDto.getCategory();
        LOGGER.info("[saveFranchiseMenu] : 데이터 추출 완료 : fid: {}, name: {}, price: {}, category: {}", fid, name, price, cid);

        Category category = null;
        Franchise franchise = null;
        Menu newMenu = new Menu();
        newMenu.setName(name);
        newMenu.setPrice(price);
        newMenu.setScore(0);
        newMenu.setInfo(menuDto.getInfo());

        Optional<Category> opCtg = categoryRepository.findById(cid);
        if (opCtg.isPresent()) {
            category = opCtg.get();
            LOGGER.info("[saveFranchiseMenu] : category 불러오기: {}", category);
        }
        Optional<Franchise> opFrc = franchiseRepository.findById(fid);
        if (opFrc.isPresent()) {
            franchise = opFrc.get();
            LOGGER.info("[saveFranchiseMenu] : franchise 불러오기: {}", franchise);
        } else return null;

        MenuImageFile mimg = null;
        if (!file.isEmpty()) {
            String[] res = s3UploadService.uploadFileToS3(file, "Images/Menu");
            mimg = new MenuImageFile();
            mimg.setMenu(newMenu);
            mimg.setUrl(res[0]);
            mimg.setStoredName(res[1]);
        }
        newMenu.setImg(mimg);
        newMenu.setCategory(category);
        newMenu.setFranchise(franchise);

        Menu savedMenu = menuRepository.save(newMenu);
        MenuResponseDto retDto = makeDto(savedMenu);
        return retDto;
    }

    @Override
    /* 메뉴 정보 요청 */
    public MenuResponseDto getMenu(Long id) throws IOException {
        LOGGER.info("[getMenu] : id = {}, 가져오기", id);
        Menu foundMenu = menuRepository.findById(id).get();
        LOGGER.info("[getMenu] : menu = {}", foundMenu);

        MenuResponseDto responseDto = makeDto(foundMenu);
        if (foundMenu.getRestaurant() != null) responseDto.setRid(foundMenu.getRestaurant().getId());
        else responseDto.setRid(foundMenu.getFranchise().getId());
        return responseDto;
    }

    @Override
    public boolean changeMenuPrice(IntChangeDto changeDto) {
        LOGGER.info("[changeMenuPrice] : 가격 수정 시작");
        Optional<Menu> foundMenu = menuRepository.findById(changeDto.getId());
        Menu fMenu = null;
        if (foundMenu.isPresent()) {
            LOGGER.info("[changeMenuPrice] : 메뉴 불러오기 성공");
            fMenu = foundMenu.get();
            fMenu.setPrice(changeDto.getValue());
            LOGGER.info("[changeMenuPrice] : 메뉴 수정 시작");
        }
        Menu chagedMenu = menuRepository.save(fMenu);

        if (chagedMenu.getPrice() != changeDto.getValue()) {
            LOGGER.info("[changeMenuPrice] : 메뉴 불러오기 실패");
            return false;
        }
        LOGGER.info("[changeMenuPrice] : 메뉴 수정 성공 = {}", chagedMenu);
        return true;
    }

    @Override
    public void deleteMenu(Long id) {
        List<Long> ids = new ArrayList<>();
        Optional<Menu> oMenu = menuRepository.findById(id);
        if (oMenu.isPresent()) {
            Menu menu = oMenu.get();
            LOGGER.info("[delete menu] menu = {}", menu);

            menu.getReviewList().forEach(review -> {
                ids.add(review.getId());
            });
            LOGGER.info("[delete menu] 리뷰 삭제");
            if (menu.getImg() != null) {
                imgRepository.deleteById(menu.getImg().getId());
                LOGGER.info("[delete menu] 이미지 정보 삭제");
            }

            menu.removeImg(menu.getImg());
        }
        reviewRepository.deleteAllByIdInBatch(ids);
        menuRepository.deleteById(id);
    }

    @Override
    public List<MenuResponseDto> getAllMenu(Long rid) {
        List<MenuResponseDto> responseDtoList = new ArrayList<>();
        menuRepository.findAllByRestaurantId(rid).forEach(m -> {
            MenuResponseDto responseDto = null;
            try {
                responseDto = makeDto(m);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            responseDtoList.add(responseDto);
        });
        return responseDtoList;
    }

    public List<MenuResponseDto> getAllMenu(int start, int size) {
        List<MenuResponseDto> responseDtoList = new ArrayList<>();
        menuRepository.findAll(PageRequest.of(start, size)).forEach(m -> {
            MenuResponseDto responseDto = null;
            try {
                responseDto = makeDto(m);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            responseDtoList.add(responseDto);
        });
        return responseDtoList;
    }

    public List<MenuResponseDto> getListFranchiseMenu(Long fid) {
        List<MenuResponseDto> resDtoList = new ArrayList<>();
        LOGGER.info("[getListFranchiseMenu] : 탐색 시작");
        menuRepository.findAllByFranchiseId(fid).forEach(m -> {
            try {
                resDtoList.add(makeDto(m));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        LOGGER.info("[getListFranchiseMenu] : 탐색 종료");
        return resDtoList;
    }

    @Override
    public List<MenuResponseDto> getAllFranchiseMenu(Long fid) {
        List<MenuResponseDto> responseDtoList = new ArrayList<>();
        menuRepository.findAllByRestaurant_FranchiseId(fid).forEach(m -> {
            try {
                responseDtoList.add(makeDto(m));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return responseDtoList;
    }

    public MenuResponseDto makeDto(Menu menu) throws IOException {
        LOGGER.info("[makeDto] DTO 생성 시작 : {}", menu);

        MenuResponseDto mrd = MenuResponseDto.builder()
                .price(menu.getPrice())
                .category(menu.getCategory().getName())
                .name(menu.getName())
                .score(0)
                .info(menu.getInfo())
                .build();
        if (menu.getRestaurant() != null) {
            mrd.setRid(menu.getRestaurant().getId());
            mrd.setR_name(menu.getRestaurant().getName());
        }
        if (menu.getReviewList().size() != 0)
            mrd.setScore(menu.getScore() / menu.getReviewList().size());
        if (menu.getUrl() != null)
            mrd.setUrl(menu.getUrl());
        if (menu.getImg() != null) {
            mrd.setImg_url(s3UploadService.getObject(menu.getImg().getStoredName()));
        }
        LOGGER.info("[makeDto] DTO 생성 완료");
        return mrd;
    }

    public void sum() {
        List<Menu> mlist = new ArrayList<>();
        menuRepository.findAll().forEach(menu -> {
            menu.sumScore();
            mlist.add(menu);
        });
        menuRepository.saveAllAndFlush(mlist);
    }

    public void uploadMenuImage(long menu_id, MultipartFile file) {
        Menu menu = menuRepository.findById(menu_id).orElse(null);
        if (menu != null && file != null) {
            MenuImageFile mimg = null;
            String[] res = s3UploadService.uploadFileToS3(file, "Images/Menu");
            mimg = new MenuImageFile();
            mimg.setMenu(menu);
            mimg.setUrl(res[0]);
            mimg.setStoredName(res[1]);
            menu.setImg(mimg);
            menuRepository.save(menu);
        }
    }
    public MenuResponseDto modify(MenuModifyDto menuDto, MultipartFile file) throws IOException {
        Menu menu = menuRepository.findById(menuDto.getMenu_id()).orElse(null);
        if(menu!=null){
            if(menu.getImg()!=null){
                LOGGER.info("[modify] 기존 이미지 정보 삭제");
                MenuImageFile imgData = menu.getImg();
                try {
                    imgRepository.deleteById(menu.getImg().getId());
                }catch (RuntimeException e){
                    throw e;
                }
            }
            if(file!=null && !file.isEmpty()){
                LOGGER.info("[modify] 새로운 이미지 저장 시도");
                String[] imgRes = s3UploadService.uploadFileToS3(file, "Images/Menu");
                LOGGER.info("[modify] 새로운 이미지 S3에 저장 완료. 데이터 생성 시작");
                MenuImageFile imgData = new MenuImageFile();
                imgData.setUrl(imgRes[0]);
                imgData.setStoredName(imgRes[1]);
                imgData.setMenu(menu);
                menu.setImg(imgData);
                LOGGER.info("[modify] 새로운 이미지에 대한 정보 생성 완료");
            }
            menu.setName(menuDto.getName());
            menu.setPrice(menu.getPrice());
            menu.setInfo(menuDto.getInfo());

            LOGGER.info("[modify] 메뉴 정보 저장");
            Menu savedMenu =menuRepository.save(menu);
            LOGGER.info("[modify] 메뉴 정보 저장 완료 -> {}", savedMenu.toString());

            return makeDto(savedMenu);
        }
        return null;
    }

    @Override
    public MenuListDto makeListDto(Menu menu) {
        if (menu == null)
            return null;
        MenuListDto menuListDto = MenuListDto.builder()
                .menu_id(menu.getId())
                .menu_score(menu.getScore())
                .menu_price(menu.getPrice())
                .menu_category(menu.getCategory().getName())
                .menu_name(menu.getName())
                .youtube_url(menu.getUrl())
                .build();
        if(menu.getImg()!= null){
            menuListDto.setImg_url(menu.getImg().getUrl());
        }

        return menuListDto;
    }
}
