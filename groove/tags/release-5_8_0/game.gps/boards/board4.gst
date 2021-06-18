<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="boards.board4">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n3">
            <attr name="layout">
                <string>431 72 80 15</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>431 171 82 15</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>599 72 22 15</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>601 167 22 15</string>
            </attr>
        </node>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>id:startRed</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>color:255,0,0</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:Pos</string>
            </attr>
        </edge>
        <edge to="n6" from="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n5" from="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>id:startBlue</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>color:0,0,255</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:Pos</string>
            </attr>
        </edge>
        <edge to="n5" from="n4">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n3" from="n4">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:Pos</string>
            </attr>
        </edge>
        <edge to="n6" from="n5">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:Pos</string>
            </attr>
        </edge>
        <edge to="n4" from="n6">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
    </graph>
</gxl>
